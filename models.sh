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
        "arr"
        "bt"
        "cd"
        "contains"
        "ctree"
        "grade"
        "deadlock"
        "dll"
        "remove"
        "sll"
)

# Models

declare -g -A arr=(
        [model_name]="arr"
        [model_path]="${MODEL_DIR}/arr.als"
        [fragment_path]="${FRAGMENT_DIR}/arr.txt"
        [test_path]="${TEST_DIR}/arr.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A bt=(
        [model_name]="bt"
        [model_path]="${MODEL_DIR}/bt.als"
        [fragment_path]="${FRAGMENT_DIR}/bt.txt"
        [test_path]="${TEST_DIR}/bt.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A cd=(
        [model_name]="cd"
        [model_path]="${MODEL_DIR}/cd.als"
        [fragment_path]="${FRAGMENT_DIR}/cd.txt"
        [test_path]="${TEST_DIR}/cd.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A contains=(
        [model_name]="contains"
        [model_path]="${MODEL_DIR}/contains.als"
        [fragment_path]="${FRAGMENT_DIR}/contains.txt"
        [test_path]="${TEST_DIR}/contains.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A ctree=(
        [model_name]="ctree"
        [model_path]="${MODEL_DIR}/ctree.als"
        [fragment_path]="${FRAGMENT_DIR}/ctree.txt"
        [test_path]="${TEST_DIR}/ctree.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A deadlock=(
        [model_name]="deadlock"
        [model_path]="${MODEL_DIR}/deadlock.als"
        [fragment_path]="${FRAGMENT_DIR}/deadlock.txt"
        [test_path]="${TEST_DIR}/deadlock.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A dll=(
        [model_name]="dll"
        [model_path]="${MODEL_DIR}/dll.als"
        [fragment_path]="${FRAGMENT_DIR}/dll.txt"
        [test_path]="${TEST_DIR}/dll.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A grade=(
        [model_name]="grade"
        [model_path]="${MODEL_DIR}/grade.als"
        [fragment_path]="${FRAGMENT_DIR}/grade.txt"
        [test_path]="${TEST_DIR}/grade.als"
        [scope]="4"
        [sol_num]="1"
)

declare -g -A remove=(
        [model_name]="remove"
        [model_path]="${MODEL_DIR}/remove.als"
        [fragment_path]="${FRAGMENT_DIR}/remove.txt"
        [test_path]="${TEST_DIR}/remove.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A sll=(
        [model_name]="sll"
        [model_path]="${MODEL_DIR}/sll.als"
        [fragment_path]="${FRAGMENT_DIR}/sll.txt"
        [test_path]="${TEST_DIR}/sll.als"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A t=(
        [model_name]="t"
        [model_path]="${MODEL_DIR}/t.als"
        [fragment_path]="${FRAGMENT_DIR}/t.txt"
        [test_path]="${TEST_DIR}/t.als"
        [scope]="3"
        [sol_num]="10"
)
