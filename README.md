# A Multi-Agent Simulation of Disease Spread to Investigate the Effectiveness of Intervention Strategies

This project was developed as part of a third year dissertation project for the Department of Computer Science at the University of Warwick.

## Abstract

The modelling of a disease is imperative in the design of intervention strategies to manage outbreaks and mitigate public health risks. Accurate disease modelling is very challenging however, with classical disease models often involving significant assumptions which can fail to capture the true complexity of disease dynamics, leading to misleading results. Based on the theory of a multi-agent systems, this project poses an multi-agent simulation of disease spread, in which a disease is simulated spreading across a synthetic population in a virtual environment, modelled using real-world geographic data. A realistic population is simulated, exhibiting complex behaviour based on diverse characteristics as informed by parameters, which can better encapsulate the heterogeneity and complexity of a real population. A wide range of intervention strategies can then be configured and simulated, allowing for investigation into their effectiveness at reducing the spread of a disease. Additionally, an intuitive user interface and real-time visualisation tools are developed with non-technical users in mind such as policymakers, which most existing simulations lack. Promising results are produced by the simulation, being comparable to real-world historical data for past outbreaks, and providing useful insights into the effectiveness of a range of intervention strategies.

## Technologies & Libraries

- **Language:** Java
- **Build Tool:** Maven
- **GUI Libraries:** JavaFX, Java AWT, AtlantaFX
- **GIS Libraries:** GeoTools, JTS Topology Suite
- **Version Control:** Git/GitHub

## Installation & Running

Java 17 or later is required to run the project.

To install the project's dependencies, run:

    mvn clean install
        
To start the application, run:

    mvn javafx:run

Some sample simulation parameters are loaded by default. These parameters do not attempt to model a real population or disease, but are given for demonstration purposes. The environment map loaded is an area of Warwick, the shapefiles for which can be found in `maps/warwick`.
