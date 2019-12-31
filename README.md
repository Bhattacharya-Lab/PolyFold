![polyfold](logo.png)

PolyFold is an interactive visual simulator for distance-based protein folding
dynamics. The goal of PolyFold is to give citizen scientists and academic
scientists alike an intuitive and visually appealing tool which elucidates the
process of folding proteins with common machine learning optimization
techniques in real time. It is the hope of the PolyFold team that this tool will
serve to fill out the gaps in the user's intuition and understanding of the way
certain types of proteins respond to certain types of optimization strategies.

No prior scientific background is needed to work with PolyFold. It is built
entirely with human intuition in mind.

## How to Compile and Run the Source Code

It is extremely important that you use the correct version of Java in order for PolyFold to function properly. PolyFold is designed for long term support and thus runs on Java 11. The Java 11 JDK and JRE can be downloaded at https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

### Using Make
1. Run the following line to compile the code:
=======
### Using Maven
#### Mac OS
1. If you do not have homebrew installed, you can install it with
```
$ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```
2. Install Maven
```
$ brew install maven
```
3. Export the correct environment variables. Add them to your profile or rc file for persistence
```
$ export M2_HOME=/usr/local/Cellar/maven/<version_here>
$ export M2=$M2_HOME/bin
$ export PATH=$M2:$PATH
```
4. Verify the install and correct environment variables with
```
$ mvn -v
```
5. Change into the PolyFold directory which should contain `pom.xml`
6. Compile
```
$ mvn clean javafx:jlink
```
7. Run
```
$ ./target/PolyFold/bin/launcher
```
#### Debian Based Linux
1. Use your package manager to install Maven (apt shown below)
```
$ sudo apt install maven
```
2. Export the correct environment variables. Add them to your profile or rc file for persistence
```
$ export M2_HOME=/usr/local/Cellar/maven/<version_here>
$ export M2=$M2_HOME/bin
$ export PATH=$M2:$PATH
```
3. Verify the install and correct environment variables with
```
$ mvn -v
```
4. Change into the PolyFold directory which should contain `pom.xml`
5. Compile
```
$ mvn clean javafx:jlink
```
6. Run
```
$ ./target/PolyFold/bin/launcher
```
