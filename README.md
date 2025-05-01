<p align="center"><img src="https://github.com/yurisuika/Compost/blob/Fabric-1.18.x/src/main/resources/assets/compost/icon.png?raw=true" width="256" height="256"></p>

**Compost** is a Fabric/Forge mod for Minecraft that makes composters turn your food waste into whatever you want! Have you ever thought that bone meal wasn't quite the right product that a composter should bring to the game? Well, now you can change the composter to produce dirt, apples, or even diamonds! Bring out the true essence of a composter and get dirty!

Compost 2.x has reworked the configuration system! Compost now uses "compositions" that you configure, each with a unique name that you provide! Each composition contains both a compost entry (which contains the item to generate, the chance to generate, and the min/max count of the item) and a set of world names. If the worlds set is empty, the composition is used globally! This returns the ability to have a global configuration in Compost that you can adjust on the fly! Please see the wiki for details, as the commands have been equally improved!

#### Compiling

To build from source you will need have JDK 21 to compile and, optionally, Git to clone the repository. Otherwise, download the archive and just run `./gradlew build` from the root project folder.

When using Git, just choose a directory you wish to keep the project root folder in, decide which branch you wish to compile, and then run these commands:

```shell script
git clone --branch <branch> --recursive https://github.com/yurisuika/compost.git

cd ./compost

./gradlew build
```

Afterwards, your compiled JAR will be in `./build/libs`.

#### Releases

Don't want to bother building from source? Get the releases *[right here](https://github.com/yurisuika/Compost/releases)* now!

#### Repositories

You can find Compost on both *[CurseForge](https://www.curseforge.com/minecraft/mc-mods/compost)* and *[Modrinth](https://modrinth.com/mod/compost)*!

#### Community

The one and only! Join the *[Discord](https://discord.gg/0zdNEkQle7Qg9C1H)* for the latest discussion on our server, resource pack, mods, or just to chat!
