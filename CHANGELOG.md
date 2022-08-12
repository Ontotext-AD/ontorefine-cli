# Ontotext Refine CLI

## Version 1.1.1

### New

 - Added detailed documentation about the project. The documentation describes the project main dependencies, provides details about the different supported
   commands and more.
 - Added `--help` option to each command. It will provide details about the command and the arguments that can be provided to it. The information is used in the
   documentation and to keep it up-to-date, when there are changes, we've added unit tests, which will fail, when there are mismatches.


## Version 1.1

### New

 - Introduced new GitHub Workflow for project release. The workflow builds packages and deploys them in the Ontotext Public Maven Repository. The workflow is
   triggered when new release is created though the GitHub release page.
 - Introduced new command for transformation of specific dataset to different format. In this version the command will support only transformation of CSV to RDF.
   The goal for future releases is to gradually add more types of transformations like JSON to CSV, XML to TSV, etc. Basically the only limitations are going to be
   related, to what Ontotext Refine can accept as input for the project data and in what format can that data be exported afterwards.
 - Enhanced the `rdf` command to accept parameter for SPARQL query. The query is used for the mapping of the project data to RDF and it takes precedence, when there
   is a mapping and a query provided.
 - Added new Enum for allowed RDF result formats. It will allow additional filtering the allowed formats that are going to be supported by the CLI. Also provides
   the correct message, when the format value does not match any of the allowed formats.
 
### Breaking Changes

 - The values for the `format` argument of the create project command are changed to be more user friendly. Now the user can simply pass `csv` as a value, instead
   of `"text/line-based/*sv"`.
 - For this version of the CLI, the create project command will work and accept only `.csv` files and data format. The other types of files and formats will be
   gradually over the next few releases.
 - Removed the `reconcile` command from the set of the available commands. It was incomplete and pretty much unusable in its the current state. In future releases
   it could be re-introduced and improved for actual cases, where it can be useful. 

### Changes

 - Moved the project back Java 11. Initially it was on 11, but before the first release it was downgraded to 8 due to development of an internal project. Now we are
   able to proceed with the initial plan and continue the development process with the current LTS version that is used internally in the organization.
 - All third party and utility libraries are now updated to the latest version. This is done in order to prevent any security issues due to libraries
   vulnerabilities. This will be done on regular bases in the future.
 - The version of the `ontorefine-client` library was updated to the latest in order to use the new functionalities that are developed there and to remove some
   bugs from the version `1.1.0` that was previously used.
 - Updated the CI scripts to the latest versions of the GitHub actions. Also we moved from `adopt` to `temurin` Java, because of the
   [AdoptOpenJDK transition to the Eclipse Foundation](https://blog.adoptopenjdk.net/2021/03/transition-to-eclipse-an-update/).
 - Changed the behavior of the export commands. Now they will wait for any running background processes over the project to complete, before proceeding with the data
   export. This was done mainly, because the reconciliation operation is slow and the performance depend on a lot of things. Furthermore, when the dataset is large
   it may take several minutes, even hours. So in order to guarantee that the export will be done over the updated data, the operations should be delayed.
 - Renamed some of the commands classes.
 - Separated some of the commands in their own packages in order to keep the related logic grouped.
 - Exposed some of the internal classes and enumerations in order to reuse them in the composition commands like the new `transform`.
 - A lot of common logic that was extracted in utility class so that it can be reused in more commands and processes.
 - Removed couple of values from the allowed RDF result formats, because they are not supported in the Ontotext Refine at the moment.
 - Removed the `JSON` option from the `export` command. Currently the command will support only export in default `CSV` format.

### Bug fixes

 - Fixed an issue, where if the user passes `null` or nonexistent file for the command arguments, the values will be accepted and the commands will proceed with
   the execution. This behavior caused some unexpected errors in particular for the creation of the project command. Now the file parameters for the commands are
   validated, before the execution of the commands logic.
 - Fixed the issue with the RDF export command, where if the user provides JSON document as mapping retrieved from the RDF Mapper UI, instead of the operations tab
   in the OpenRefine UI, the command fails with error. Related issue: [#8](https://github.com/Ontotext-AD/ontorefine-cli/issues/8).


## Version 1.0

### New

 - TBD
