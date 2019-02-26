#! /bin/bash
#
# helper script to create artifacts for continuous deployment to netlify.com.

docker build -t isaqb-adoc2pdf --force-rm adoc2pdf/

# index.html
docker run --rm \
   -v ${PWD}/build:/build   \
   -v ${PWD}/docs:/documents:ro \
   --entrypoint asciidoctor \
   isaqb-adoc2pdf \
   -D /build \
   -o index.html \
   /documents/index.adoc

# template-curriculum_de.html
docker run --rm \
   -v ${PWD}/build:/build   \
   -v ${PWD}/docs:/documents:ro \
   --entrypoint asciidoctor \
   isaqb-adoc2pdf \
   -a language=DE \
   -D /build \
   -o template-curriculum_de.html \
   /documents/template-curriculum.adoc

# template-curriculum_en.html
docker run --rm \
   -v ${PWD}/build:/build   \
   -v ${PWD}/docs:/documents:ro \
   --entrypoint asciidoctor \
   isaqb-adoc2pdf \
   -a language=EN \
   -D /build \
   -o template-curriculum_en.html \
   /documents/template-curriculum.adoc

# template-curriculum_de_en_with-remarks.html
docker run --rm \
   -v ${PWD}/build:/build   \
   -v ${PWD}/docs:/documents:ro \
   --entrypoint asciidoctor \
   isaqb-adoc2pdf \
   -a withRemarks \
   -a language="DE;EN" \
   -D /build \
   -o template-curriculum_de_en_with-remarks.html \
   /documents/template-curriculum.adoc

# Productive PDF DE
./adoc2pdf/adoc2pdf.sh ${PWD}/docs/template-curriculum.adoc DE prod

# Productive PDF EN
./adoc2pdf/adoc2pdf.sh ${PWD}/docs/template-curriculum.adoc EN prod
