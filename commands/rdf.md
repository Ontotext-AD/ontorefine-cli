# RDF

Exports the data of a given project in RDF format.

The command extracts and converts the data of specified project into RDF format using Ontotext Refine's internal SPARQL
engine. It supports two mechanisms for conversions. One via a JSON mapping and the other via SPARQL CONSTRUCT query.
The query takes precedence if both arguments are provided. As fallback, if neither mapping, nor SPARQL is provided, the
command will try to retrieve the mappings from the operation history for the project.
The result of the command is the project data in specific RDF format.

## Arguments

```bash
Usage:

ontorefine-cli rdf [-hV] [-f <format>] [-m <mapping>] [-q <sparql>] -u <url>
                   PROJECT

Description:
Exports the data of a project to RDF format.

Parameters:
      PROJECT               The project whose data to converted to RDF.

Options:
  -u, --url <url>           The URL of the Ontotext Refine instance to connect
                              to, e.g. http://localhost:7333.
  -q, --sparql <sparql>     A file containing SPARQL CONSTRUCT query to be used
                              for RDF conversion.
  -m, --mapping <mapping>   The mapping that will be used for the RDF
                              conversion. The file should contain JSON
                              configuration. If not provided the process will
                              try to retrieve it from the project
                              configurations.
  -f, --format <format>     Controls the format of the result. The default
                              format is 'turtle'. The allowed values are:
                              rdfxml, ntriples, turtle, turtlestar, trix, trig,
                              trigstar, binary, nquads, jsonld, rdfjson
  -h, --help                Show this help message and exit.
  -V, --version             Print version information and exit.
```

## Examples

TBD
