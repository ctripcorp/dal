@echo off
set pomPath=dal-client project path
set targetPath=%pomPath%\target\
set version=dal version here, e.g. 1.5.0
set nexusUrl=your nexus url
set repoId=your repo id

echo on
@echo Start deploy dal-client
@echo Pom path: %pomPath%
@echo Target path: %targetPath%
@echo Version: %version%
@echo Nexus url: %nexusUrl%
@echo repositoryId: %repoId%

mvn deploy:deploy-file -DpomFile=%pomPath%\pom.xml -Dfile=%targetPath%\dal-client-%version%.jar -Dsources=%targetPath%\dal-client-%version%-sources.jar -Djavadoc=%targetPath%\dal-client-%version%-javadoc.jar -Durl=%nexusUrl% -DrepositoryId=%repoId%