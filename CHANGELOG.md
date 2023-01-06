# Ontotext Refine CLI

## Version 1.2

### New

 - Introduced new argument to the create project command. It will provide the import options for the datasets, when the project is created. These options are
   carrying information how the dataset should be parsed in order to be represented as tabular data.
   The argument is called `--configurations` and it accepts path to file, containing the JSON with import options.
 - Introduced few new utility class related to the processing of the project configurations for the different commands.
 - Added few new test resources, primary related to the new functionality for the project configurations.
 - Enhanced the command for project creation to allow assigning of aliases. Now the command has additional optional argument called `--aliases`, which accept
   multiple comma separated values.
   When the argument is provided the command will use additional operation to assign the aliases. If there is an error during that process, the created project
   will be deleted. This is done in order to simulate transactional behavior, because the aliases are unique values in the context of all projects.
 - Enhanced the `transform` command, allowing it to take advantage of the project aliases functionality. Now the command has additional argument for project aliases
   assignment.
   By enabling this functionality, now the users can make SPARQL transformation queries using project alias, instead of project placeholder, in the SERVICE clause.
 - Introduced new command for updating the aliases of existing projects. The command is useful for project were created before the introduction of the aliases
   functionality.
 - Enhanced all command with additional logic that allows the usage of project aliases, when referring to a project. Now the commands will accept alias or project
   identifier for the `project` arguments. The provided value will be used to resolve the actual identifier of the project and use it for the commands.
 - Added logic for extraction of project aliases from the provided configuration file. This logic is primary used when a project is created and it allows the user
   to provide the aliases into the project configurations. Currently this logic is added in the utility file and used in `create` and `transform` commands.
   When the aliases are passed as configurations and the user provides an `--aliases` argument values, the information from the both places is merged and then used
   for the assignment operation. This allow more flexibility around the definition of the aliases on project creation.

### Changes

 - Enhanced the apply operations command to accept and work with the new types of the configurations for the project. Now the command will try to parse the provided
   JSON document and extract only the operations history in order to apply it to the project. The JSON can be as full project configuration, only the operations part
   of it, or as usual the standard operations history JSON.
 - Refactored and renamed `ExtractOperataions` command. Now the class is called `ExtractConfigurations` and it has new argument. This argument controls, which
   project configuration will be extracted. Currently the supported options are: `operations` (the default), `import-options` and `full`.
   We left the `operations` as default in order to avoid introducing breaking changes to the command. It acts as the previous command and returns the same result.
   The `import-options` will extract only the project import options and the `full` extracts both, the import options and operations history for the specified
   project.
   We've decided to do the extraction of all project configurations with single command, instead of introducing several other, because we plan to add more in the
   future. 
 - Moved some classes in their own packages in order to group the logic and the classes related to the same functionality.
 - Updated the versions of the third party dependencies as some of them had security vulnerabilities.

### Breaking Changes

 - Refactored the `transform` command to work with the new type of project configurations. The breaking change is that the previous argument for `--operations`
   is now changed to `--configurations`. It accepts all type of JSON, containing only operation history, only project import options or full set of configurations.
   The argument is enhanced version of the previous, because it will allow the users to define how the dataset should be parsed before the operations are applied to
   it, thus allowing the command to work with more dataset formats than only `CSV`.
   We've decided to replace the old argument with new one, because we plan to add more configurations to the current JSON and it will be more convenient to have one
   argument accepting all of them, instead of having a multiple arguments for each.


## Version 1.1.1

### New

 - Added detailed documentation about the project. The documentation describes the project main dependencies, provides details about the different supported
   commands and more.
 - Added `--help` option to each command. It will provide details about the command and the arguments that can be provided to it. The information is used in the
   documentation and to keep it up-to-date, when there are changes, we've added unit tests, which will fail, when there are mismatches.

### Changes

 - Removed the `N3` RDF result format as an option, when exporting in RDF. `N3` is a subset of `Turtle` and the internal library used to produce the different
   formats just writes `Turtle` for both options. This is done in order to prevent confusing moments, when the user expects different formats.
 - Improved some of the description of the commands and their arguments.
 - Improved some of the output messages from the commands.
 - Updated the version of the `ontorefine-client` library to `1.7.1`. It introduces some fixes for issues related to creation of the projects.


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
