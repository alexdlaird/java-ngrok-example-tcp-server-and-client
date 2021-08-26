.PHONY: all build clean install test

SHELL := /usr/bin/env bash
ifeq ($(OS),Windows_NT)
	ifneq (,$(findstring /cygdrive/,$(PATH)))
		GRADLE_BIN := ./gradlew
	else
		GRADLE_BIN := gradlew.bat
	endif
else
	GRADLE_BIN := ./gradlew
endif

all: build

build:
	$(GRADLE_BIN) build

clean:
	$(GRADLE_BIN) clean

test:
	$(GRADLE_BIN) test
