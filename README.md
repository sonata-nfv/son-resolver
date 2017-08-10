# son-resolver
The SONATA Service Platform resolver service to retrieve artifacts, like VM images, automatically.

## Development

To contribute to the development of the SONATA resolver, you may use the very same development workflow as for any other SONATA Github project. That is, you have to fork the repository and create pull requests.

#### Contributing

You may contribute to the editor similar to other SONATA (sub-) projects, i.e. by creating pull requests.

#### Building

The SONATA editor is written in Java and uses the Maven build tool. Thus, you can compile the resolver by typing:

```
 $ mvn clean compile
```

To build the resolver executable JAR package you may type:

```
 $ mvn clean compile package
```

This command does various things. It creates a thin executable JAR package in the ../docker/resolver/ directory. In contrast to a fat JAR, this package does not contain any dependend JAR files. Instead all dependencies have to be available in the JAVA path, e.g. in the default Maven directory. In order to facilitate the dependencies, the *package* command copies all dependencies to the ../docker/baseimage/ directory.


In addition (and because I am a lazy person), there is a Makefile that allows you to build the whole project.

```
 $ make package
```

---
#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.

- Michael Bredel (mbredel)

#### Feedback Channel

Please use the GitHub issues and the SONATA development mailing list sonata-dev@lists.atosresearch.eu for feedback.
