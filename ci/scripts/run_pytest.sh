#!/bin/bash

set -e

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPTS_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

PYTHON_SRC_DIR="${SCRIPTS_DIR}/../../python"

HELP="
Usage:
  $0 [flags] [Arguments]

    -e [CONDA_ENV] or --conda_env=[CONDA_ENV]
                              Setting conda activate environment
    -h or --help              Print help information


Use \"$0  --help\" for more information about a given command.
"

ARGS=`getopt -o "e:h" -l "conda_env::,help" -n "$0" -- "$@"`

eval set -- "${ARGS}"

while true ; do
        case "$1" in
                -e|--conda_env)
                        case "$2" in
                                "") echo "Option conda_env, no argument"; exit 1 ;;
                                *)  CONDA_ENV=$2 ; shift 2 ;;
                        esac ;;
                -h|--help) echo -e "${HELP}" ; exit 0 ;;
                --) shift ; break ;;
                *) echo "Internal error!" ; exit 1 ;;
        esac
done

if [[ -n ${CONDA_ENV} ]]; then
    eval "$(conda shell.bash hook)"
    conda activate ${CONDA_ENV}

    CONDA_ENV_PYTHON_FILE="${SCRIPTS_DIR}/../yaml/conda_env_python.yml"
    if [[ -f "${CONDA_ENV_PYTHON_FILE} ]]; then
        conda install  -c conda-forge -q -y --file "${CONDA_ENV_PYTHON_FILE}"
    fi

fi

pushd "${PYTHON_SRC_DIR}"

pytest
# --ignore "examples/example_test.py" \
# --ignore-glob "examples/*"

popd
