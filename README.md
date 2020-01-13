![polyfold](Assets/logo.png)

PolyFold is an interactive visual simulator for distance-based protein folding
dynamics. The goal of PolyFold is to give citizen scientists and academic
scientists alike an intuitive and visually appealing tool which elucidates the
process of folding proteins with common machine learning optimization
techniques in real time. It is the hope of the PolyFold team that this tool will
serve to fill out the gaps in the user's intuition and understanding of the way
certain types of proteins respond to certain types of optimization strategies.

No prior scientific background is needed to work with PolyFold. It is built
entirely with human intuition in mind.

## Dataset
PolyFold includes with it a set of 15 curated Residue-Residue files for small protein targets. Each gif below is a visualization of the interaction map for one of these proteins as it is optimized. They each also link to the Residue-Residue files for download. 


<img src="Assets/1aapA.gif" height="200"/> | <img src="Assets/1atzA.gif" height="200"/> | <img src="Assets/1brfA.gif" height="200"/>
1aapA | 1atzA | 1brfA
<p align="middle">
  <img src="Assets/1c9oA.gif" height="200"/>
  <img src="Assets/1cc8A.gif" height="200"/>
  <img src="Assets/1ctfA.gif" height="200"/>
</p>
<p align="middle">
  <img src="Assets/1guuA.gif" height="200"/>
  <img src="Assets/1h98A.gif" height="200"/>
  <img src="Assets/1jo8A.gif" height="200"/>
</p>
<p align="middle">
  <img src="Assets/1ku3A.gif" height="200"/>
  <img src="Assets/1kw4A.gif" height="200"/>
  <img src="Assets/1m8aA.gif" height="200"/>
</p>
<p align="middle">
  <img src="Assets/1t8kA.gif" height="200"/>
  <img src="Assets/1tifA.gif" height="200"/>
  <img src="Assets/1vfyA.gif" height="200"/>
</p>

## How to Compile and Run the Source Code

It is extremely important that you use the correct version of Java in order for PolyFold to function properly. PolyFold is designed for long term support and thus runs on Java 11. The Java 11 JDK and JRE can be downloaded at https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

### Environment Variables
PolyFold depends on two environment variables in order to function properly - JAVA_HOME and JAVAFX_HOME. 

The JAVA_HOME variable needs to point to your installation of Java 11. More specifically it needs to point to the parent directory of the `bin` directory. On my Mac, this directory happens to be:
```
/Library/Java/JavaVirtualMachines/jdk-11.0.5.jdk/Contents/Home
```
On my Debian machine, this directory happens to be:
```
/usr/lib/jvm/jdk-11.0.5
```

The JAVAFX_HOME variable needs to point to your JavaFX installation. More specifically, it points to the `lib` directory within your JavaFX installation. On my Mac, this directory happens to be:
```
/Library/Java/JavaFX/javafx-sdk-11.0.2/lib
```
On my Debian machine, this directory happens to be:
```
/usr/lib/jvm/javafx-sdk-11.0.2/lib
```

So, in my case, in order to set the proper environment variables on my Mac, I have added the following to the RC file for my shell:
```
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.5.jdk/Contents/Home
export JAVAFX_HOME=/Library/Java/JavaFX/javafx-sdk-11.0.2/lib
```
NOTE: all the above listed directories could vary on your own machine. 

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
$ export M2_HOME=/usr/share/maven/
$ export M2=$M2_HOME/bin
$ export PATH=$M2:$PATH
```
3. Verify the install and correct environment variables with
```
$ mvn -v
```
4. Change into the PolyFold directory which should contain `pom.xml`
5. Compile. If lots of text litters your terminal screen don't panic! Maven may need to download supplementary files here.
```
$ mvn clean javafx:jlink
```
6. Run
```
$ ./target/PolyFold/bin/launcher
```
