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
        "dijkstra"
        "dll"
        "remove"
        "sll"
)

# Models

declare -g -A arr=(
        [model_name]="arr"
        [model_path]="${MODEL_DIR}/arr.als"
        [fragment_path]="${FRAGMENT_DIR}/arr.frg"
        [test_path]="${TEST_DIR}/arr.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A bt=(
        [model_name]="bt"
        [model_path]="${MODEL_DIR}/bt.als"
        [fragment_path]="${FRAGMENT_DIR}/bt.frg"
        [test_path]="${TEST_DIR}/bt.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A cd=(
        [model_name]="cd"
        [model_path]="${MODEL_DIR}/cd.als"
        [fragment_path]="${FRAGMENT_DIR}/cd.frg"
        [test_path]="${TEST_DIR}/cd.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A contains=(
        [model_name]="contains"
        [model_path]="${MODEL_DIR}/contains.als"
        [fragment_path]="${FRAGMENT_DIR}/contains.frg"
        [test_path]="${TEST_DIR}/contains.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A ctree=(
        [model_name]="ctree"
        [model_path]="${MODEL_DIR}/ctree.als"
        [fragment_path]="${FRAGMENT_DIR}/ctree.frg"
        [test_path]="${TEST_DIR}/ctree.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A dijkstra=(
        [model_name]="dijkstra"
        [model_path]="${MODEL_DIR}/dijkstra.als"
        [fragment_path]="${FRAGMENT_DIR}/dijkstra.frg"
        [test_path]="${TEST_DIR}/dijkstra.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A dll=(
        [model_name]="dll"
        [model_path]="${MODEL_DIR}/dll.als"
        [fragment_path]="${FRAGMENT_DIR}/dll.frg"
        [test_path]="${TEST_DIR}/dll.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A grade=(
        [model_name]="grade"
        [model_path]="${MODEL_DIR}/grade.als"
        [fragment_path]="${FRAGMENT_DIR}/grade.frg"
        [test_path]="${TEST_DIR}/grade.tst"
        [scope]="4"
        [sol_num]="1"
)

declare -g -A remove=(
        [model_name]="remove"
        [model_path]="${MODEL_DIR}/remove.als"
        [fragment_path]="${FRAGMENT_DIR}/remove.frg"
        [test_path]="${TEST_DIR}/remove.tst"
        [scope]="3"
        [sol_num]="1"
)

declare -g -A sll=(
        [model_name]="sll"
        [model_path]="${MODEL_DIR}/sll.als"
        [fragment_path]="${FRAGMENT_DIR}/sll.frg"
        [test_path]="${TEST_DIR}/sll.tst"
        [scope]="3"
        [sol_num]="1"
)
