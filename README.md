[![Java CI with Maven](https://github.com/Ontotext-AD/ontorefine-cli/actions/workflows/CI.yaml/badge.svg)](https://github.com/Ontotext-AD/ontorefine-cli/actions/workflows/CI.yaml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Ontotext-AD_ontorefine-cli&metric=alert_status)](https://sonarcloud.io/dashboard?id=Ontotext-AD_ontorefine-cli)

# Ontotext Refine CLI (ontorefine-cli)

A command-line interface for execution of operations in Ontotext Refine.

The library uses another public project maintained by Ontotext called [ontorefine-client](https://github.com/Ontotext-AD/ontorefine-client).
It provides an intuitive and convenient API for communication with Ontotext Refine via HTTP requests.

Most of the commands that CLI provides are implemented by directly exposing the operations from the client library, but
there are some that are actually composition of the one or more client operations in order to complete more complex 
workflow. An example of such workflow is the transformation of dataset to RDF.

## Installation

```xml
 <dependency>
     <groupId>com.ontotext</groupId>
     <artifactId>ontorefine-cli</artifactId>
     <version>1.2.0</version>
 </dependency>
```

**NOTE**: Currently it isn't possible to use the CLI as stand alone tool. We are planning to make an executable JAR for
some of the future releases.

## Commands

    Usage: ontorefine-cli [-hV] [COMMAND]

    -h, --help      Show this help message and exit.
    -V, --version   Print version information and exit.

    Commands:

    create            Creates a new project from a file.
    delete            Deletes a project from OntoRefine.
    export            Exports the data of a project in CSV or JSON format.
    extract           Extracts specific project configuration in JSON format.
    apply             Applies transformation operations to a project.
    register-service  Registers an additional reconciliation service.
    rdf               Exports the data of a project to RDF format.
    transform         Transforms given dataset into different data format.
    refine-version    Retrieves the version of the Ontotext Refine instance.
    update-aliases    Updates project aliases. The command can add and remove values in a single invocation.
    help              Displays help information about the specified command.

More details about the individual commands can be seen in the documents in [commands](commands) directory.

## Development & Releases

Please checkout the [DEV](DEV.md) document for more information on the topics.

## License

Please refer to the [LICENSE](LICENSE) file for details.
