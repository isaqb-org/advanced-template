package org.isaqb.asciidoc;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static org.asciidoctor.Asciidoctor.Factory.create;

public class Main {

    private static final String PROJECT_VERSION = "projectVersion";
    private static final String CURRICULUM_FILE_NAME = "curriculumFileName";
    private static final String VERSION_DATE = "versionDate";
    private static final String LANGUAGES = "languages";
    private static final String[] FORMATS = {"html", "pdf"};

    private static final String LANGUAGE_SEPERATOR = ",";

    private static final String SOURCE_DIR = "./docs/";
    private static final String BASE_DIR = SOURCE_DIR;
    private static final String OUTPUT_DIR = "./build/";

    private static final String ADOC = "adoc";
    private static final String HTML = "html";
    private static final String PDF = "pdf";

    public static void main(final String[] args) {
        Objects.requireNonNull(System.getProperty(PROJECT_VERSION));
        Objects.requireNonNull(System.getProperty(CURRICULUM_FILE_NAME));
        Objects.requireNonNull(System.getProperty(VERSION_DATE));
        Objects.requireNonNull(System.getProperty(LANGUAGES));

        final String projectVersion = System.getProperty(PROJECT_VERSION);
        final String curriculumFileName = System.getProperty(CURRICULUM_FILE_NAME);
        final String versionDate = System.getProperty(VERSION_DATE);
        final String[] languages = System.getProperty(LANGUAGES).split(LANGUAGE_SEPERATOR);

        System.out.printf("Source Directory: %s\n", new File(SOURCE_DIR).getAbsolutePath());
        System.out.printf("Base Directory: %s\n", new File(BASE_DIR).getAbsolutePath());
        System.out.printf("Output Directory: %s\n", new File(OUTPUT_DIR).getAbsolutePath());
        System.out.printf("Property PROJECT_VERSION: %s\n", projectVersion);
        System.out.printf("Property CURRICULUM_FILE_NAME: %s\n", curriculumFileName);
        System.out.printf("Property VERSION_DATE: %s\n", versionDate);
        System.out.printf("Property LANGUAGES: %s\n", String.join(LANGUAGE_SEPERATOR, languages));

        Stream.of(languages).forEach(language -> convert(
                projectVersion,
                curriculumFileName,
                versionDate,
                language));
    }

    private static void convert(
            final String projectVersion,
            final String curriculumFileName,
            final String versionDate,
            final String language) {
        Stream.of(FORMATS).forEach(format -> convert(
                projectVersion,
                curriculumFileName,
                versionDate,
                language,
                format));
    }

    private static void convert(
            final String projectVersion,
            final String curriculumFileName,
            final String versionDate,
            final String language,
            final String format) {
        try (final Asciidoctor asciidoctor = create()) {
            final List<String> fileNames = Arrays.asList(curriculumFileName, "index");
            final Attributes attributes = toAttributes(
                    projectVersion,
                    curriculumFileName,
                    versionDate,
                    language);
            asciidoctor.convertDirectory(
                    fileNames.stream()
                            .map(it -> "%s%s.%s".formatted(SOURCE_DIR, it, ADOC))
                            .map(File::new)
                            .toList(),
                    Options.builder()
                            .baseDir(new File(BASE_DIR))
                            .backend(toBackend(format))
                            .mkDirs(true)
                            .attributes(attributes)
                            .standalone(true)
                            .toDir(new File(OUTPUT_DIR))
                            .safe(SafeMode.UNSAFE)
                            .build());
            renameResultAccordingToLanguage(curriculumFileName, format, language);
        }
    }

    private static Attributes toAttributes(
            final String projectVersion,
            final String curriculumFileName,
            final String versionDate,
            final String language) {
        final String fileVersion = "%s - %s".formatted(projectVersion, language);
        final String documentVersion = "%s-%s".formatted(fileVersion, versionDate);

        final Map<String, Object> attributes = new HashMap<>() {{
            put("icons"             , "font");
            put("version-label"     , "");
            put("revnumber"         , fileVersion);
            put("revdate"           , versionDate);
            put("document-version"  , documentVersion);
            put("currentDate"       , versionDate);
            put("language"          , language);
            put("curriculumFileName", curriculumFileName);
            put("debug_adoc"        , false);
            put("pdf-themesdir"     , "../pdf-theme/themes");
            put("pdf-fontsdir"      , "../pdf-theme/fonts");
            put("pdf-theme"         , "isaqb");
            put("stylesheet"        , "../html-theme/adoc-github.css");
            put("stylesheet-dir"    , "../html-theme");
            put("allow-uri-read"    , true);
        }};

        return Attributes.builder().attributes(attributes).build();
    }

    private static String toBackend(final String format) {
        return switch (format) {
            case HTML -> "html5";
            case PDF -> PDF;
            default -> throw new IllegalArgumentException("Unknown target format %s".formatted(format));
        };
    }

    private static void renameResultAccordingToLanguage(
            final String fileName,
            final String format,
            final String language) {
        final File original = new File("%s%s.%s".formatted(OUTPUT_DIR, fileName, format));
        final File renamed = new File("%s%s-%s.%s".formatted(OUTPUT_DIR, fileName, language.toLowerCase(), format));
        if (!original.exists()) {
            System.err.printf("Failed to rename result file %s as it does not exist", original.getAbsolutePath());
        } else if (!original.renameTo(renamed)) {
            System.err.printf("Failed to rename result file %s to %s", original.getName(), renamed.getName());
        }
        original.deleteOnExit();
    }
}
