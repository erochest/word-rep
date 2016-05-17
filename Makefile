
models: models/en-sent.bin models/en-token.bin

models/en-sent.bin:
	curl --output $@ http://opennlp.sourceforge.net/models-1.5/en-sent.bin

models/en-token.bin:
	curl --output $@ http://opennlp.sourceforge.net/models-1.5/en-token.bin

.PHONY: models

