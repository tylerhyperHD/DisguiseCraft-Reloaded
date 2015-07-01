## How to build DisguiseCraft

### Using Maven

The pom.xml file is already configured to download dependencies, compile, and package this plugin. Just use the goals "clean package" and the DisguiseCraft jar file will be put in the /target folder.

### Manually

Firstly, you'll need to get a hold of the dependencies ProtocolLib and Spigot. Then compile the whole pgDev.bukkit.DisguiseCraft package and subpackages with the dependencies in the classpath. I believe the command would look something like "javac -classpath .:ProtocollLib.jar:Spigot.jar pgDev/bukkit/DisguiseCraft/*.java". The classpath would use a : or ; depending on which operating system you are using. To package, place the compiled files into a .jar file alongside the plugin.yml file.

DisguiseCraft is under a GPLv3 License found at:
http://dev.bukkit.org/licenses/7-gnu-general-public-license-version-3-gplv3/