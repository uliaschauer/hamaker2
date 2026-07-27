# Hamaker2

Hamaker2 is a Java/Swing desktop application to predict the stability of colloidal suspensions within DLVO theory. It features a variety of interaction models for electrostatic, dispersion and steric interactions and can easily be extended via plugin models.

> This project was originally developed in NetBeans using an Ant-based build. It has since been migrated to Maven to make it easier to build, package, and maintain outside of a specific IDE.

## How to cite

When publishing results obtained using the Hamaker software package, please cite the following reference:

U. Aschauer; O. Burgos-Montes; R. Moreno; P. Bowen, Journal of Dispersion Science and Technology, 32(4), 470-479 **(2011).**

## Documentation

The full user manual (theoretical background, built-in interaction models, and a guide to writing your own plug-in models) is available at [`doc/manual.md`](doc/manual.md).

## Requirements

- **Java 21 or later** (JDK, not just a JRE — needed to build; any modern JRE 21+ works to just run it)
- **Maven 3.6+**

## Building

```bash
mvn clean package
```

This produces a runnable jar with all dependencies bundled:

```
target/Hamaker2-jar-with-dependencies.jar
```

## Running

```bash
java -jar target/Hamaker2-jar-with-dependencies.jar
```

## Pre-built binaries

Pre-built binaries are not signed and may require you to bypass system security.

## Project structure

```
src/main/java/hamaker2/   Application source code
pom.xml                   Maven build configuration
```

## Dependencies

| Library | Purpose |
|---|---|
| [JFreeChart](https://www.jfree.org/jfreechart/) | 2D charting |
| [JOGL](https://jogamp.org/jogl/www/) | Java bindings for OpenGL (3D rendering) |
| [Swing Layout Extensions](https://mvnrepository.com/artifact/net.java.dev.swing-layout/swing-layout) | Legacy layout manager used by NetBeans-generated GUI forms |

## Notes for contributors

- GUI forms were built with NetBeans's GUI builder (Matisse) and use the legacy `org.jdesktop.layout` package rather than the modern `javax.swing.GroupLayout`. If you edit forms in NetBeans, this should continue to work as-is; editing generated `// GEN-BEGIN` / `// GEN-END` blocks by hand in another IDE is not recommended.
- The project can still be opened directly in NetBeans (File → Open Project) since it's a standard Maven project.

## License

MIT
