#!/usr/bin/env bash

trap "exit" INT

_ASKETCH_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

. ${_ASKETCH_DIR}/models.sh

# Main functionality

function asketch.build() {
        mvn clean package
}

function asketch.run() {
        local model_path="${1}"; shift
        local fragment_path="${1}"; shift
        local test_path="${1}"; shift
        local scope="${1}"; shift
        local solution_num="${1}"; shift

        java -Xmx8g -cp "${JAR_PATH}" asketch.ASketch\
             "${model_path}"\
             "${fragment_path}"\
             "${test_path}"\
             "${scope}"\
             "${solution_num}"
}

function asketch.run.model() {
        eval $(obj.unpack "${1}"); shift

        local result_path="${RESULT_DIR}/${obj[model_name]}.txt"
        echo "Run ${obj[model_name]} and searching for ${obj[sol_num]} solutions."
        asketch.run "${obj[model_path]}" "${obj[fragment_path]}" "${obj[test_path]}" "${obj[scope]}" "${obj[sol_num]}" | tee "${result_path}"
}

function asketch.run.all() {
        echo "Running ASketch for all models"
        model.foreach asketch.run.model MODELS[@]
}

function model.foreach() {
        local fun="${1}"; shift
        declare -a array=("${!1}"); shift
        for model in ${array[@]}; do
	        ${fun} "$(declare -p ${model})" "$@"
        done
}

function obj.unpack() {
        local aa="${1}"; shift
        echo "declare -A obj="${aa#*=}
}

# ----------
# Main.

case $1 in
        --build) shift;
                asketch.build "$@";;
        --run) shift;
                asketch.run "$@";;
        --run-model) shift;
		asketch.run.model "$(declare -p "${1}")";;
        --run-all) shift;
		asketch.run.all "$@";;
        *)
	        echo "ERROR: Incorrect arguments: $@"
	        exit 1;;
esac
