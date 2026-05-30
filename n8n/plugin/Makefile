.PHONY: install format lint lintfix build dev clean check all

all: install check
check: format lintfix lint build

install: # install nodes
	npm install

format:
	npm run format

lint:
	npm run lint

lintfix:
	npm run lintfix

build:
	npm run build

dev: # live recompile
	npm run dev

clean:
	rm -rf dist node_modules
