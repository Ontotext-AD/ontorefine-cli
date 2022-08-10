# OntoRefine CLI

## Version 1.1

### New

 - Introduced new GitHub Workflow for project release. The workflow builds packages and deploys them in the Ontotext Public Maven Repository. The workflow is
   triggered when new release is created though the GitHub release page.
 
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

### Bug fixes

 - Fixed an issue, where if the user passes `null` or nonexistent file for the command arguments, the values will be accepted and the commands will proceed with
   the execution. This behavior caused some unexpected errors in particular for the creation of the project command. Now the file parameters for the commands are
   validated, before the execution of the commands logic.


## Version 1.0

### New

 - TBD
