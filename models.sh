#!/usr/bin/env bash

_ASKETCH_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

LIBS_DIR="${_ASKETCH_DIR}/libs"
EXPERIMENT_DIR="${_ASKETCH_DIR}/experiments"
MODEL_DIR="${EXPERIMENT_DIR}/models"
FRAGMENT_DIR="${EXPERIMENT_DIR}/fragments"
TEST_DIR="${EXPERIMENT_DIR}/tests"
RESULT_DIR="${EXPERIMENT_DIR}/results"
mkdir -p "${RESULT_DIR}"

JAR_PATH="${_ASKETCH_DIR}/target/asketch-1.0-jar-with-dependencies.jar:${LIBS_DIR}/alloy.jar:${LIBS_DIR}/aparser-1.0.jar"

MODELS=(
        "sll"
)

# Models
declare -g -A sll=(
        [model_name]="sll"
        [model_path]="${MODEL_DIR}/sll.als"
        [fragment_path]="${FRAGMENT_DIR}/sll.frg"
        [test_path]="${TEST_DIR}/sll.tst"
        [sol_num]="10"
)
