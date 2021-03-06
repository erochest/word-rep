
run: models/skip-gram.bin

break-corpus: models/en-sent.bin
	sbt "run break-corpus --input-dir corpus --output-dir output"

models: models/en-sent.bin models/en-token.bin

models/en-sent.bin:
	curl --output $@ http://opennlp.sourceforge.net/models-1.5/en-sent.bin

models/en-token.bin:
	curl --output $@ http://opennlp.sourceforge.net/models-1.5/en-token.bin

models/skip-gram.bin: models/en-token.bin
	sbt "run train --input-dir output/training --output models/skip-gram.bin"


.PHONY: run break-corpus models

