# Oscillator [![Build Status](https://travis-ci.org/juanmbellini/oscillator.svg?branch=master)](https://travis-ci.org/juanmbellini/oscillator)

First part of the fourth System Simulations project: A damped harmonic oscillator

## Getting started

These instructions will install the system in your local machine.

### Prerequisites

1. Clone the repository, or download source code

	```
	$ git clone https://github.com/juanmbellini/oscillator
	```
	or

	```
	$ wget https://github.com/juanmbellini/oscillator/archive/master.zip
	```

2. Install Maven, if you haven't yet

    #### Mac OS X

    ```
    $ brew install maven
    ```

    #### Ubuntu

    ```
    $ sudo apt-get install maven
    ```

    #### Other OSes
    Check [Maven website](https://maven.apache.org/install.html).


### Installing

1. Change working directory to project root (i.e where pom.xml is located):

    ```
    $ cd <project-root>
    ```

2. Let maven resolve dependencies:

    ```
    $ mvn dependency:resolve -U
    ```

3. Create jar file

    ```
    $ mvn clean package
    ```
    **Note:** The jar file will be under ``` <project-root>/target ```


## Usage

You can run the simulation with the following command:


```
$ java -jar <path-to-jar> [arguments]
```

In order to customize the simulation, you can specify different types of parameters.

### Oscillating particle mass
You can specify the particle's mass with the ```--custom.system.particle-mass``` argument.

For example, if you want a mass of ```150.0```, you would execute:

```
$ java -jar <path-to-jar> --custom.system.particle-mass=150.0
```

**This argument has kilgrams [kg] as unit.**

**The default value is 70.0.**

### Starting position
You can specify the particle's starting position (in the 'x' axis) with the ```--custom.system.initial-x``` argument.
For example, if you want to start at ```0.80```, you would execute:

```
$ java -jar <path-to-jar> --custom.system.initial-x=0.8
```

**This argument has meters [m] as unit.**

**The default value is 70.0.**

### Spring constant
You can specify the spring's constant with the ```--custom.system.spring-constant``` argument.
For example, if you want a spring constant of ```2000```, you would execute:

```
$ java -jar <path-to-jar> --custom.system.spring-constant=2000
```

**This argument has kilgroms over square seconds [kg/s^2] as unit.**

**The default value is 10000.0.**

### Viscous Damping Coefficient
You can specify the viscous damping coefficient with the ```--custom.system.viscous-damping-coefficient``` argument.
For example, if you want a coefficient of ```50```, you would execute:

```
$ java -jar <path-to-jar> --custom.system.viscous-damping-coefficient=50
```

**This argument has kilgroms over seconds [kg/s] as unit.**

**The default value is 100.0.**

### Integration strategy
You can specify the integration strategy (i.e Verlet-Original, Verlet-Trick, Beeman or 5th order Gear) with the ```--custom.simulation.strategy``` argument.
For example, if you want to use Verlet-Original, you would execute:

```
$ java -jar <path-to-jar> --custom.simulation.time-step=VERLET
```

**There is no default value.**

**Note: Possible values are: VERLET, VERLET_TRICK, BEEMAN or GEAR.**

### Time step
You can specify the simulation time step with the ```--custom.simulation.time-step``` argument.
For example, if you want a time step of ```0.01```, you would execute:

```
$ java -jar <path-to-jar> --custom.simulation.time-step=0.01
```

**This argument has seconds [s] as unit.**

**The default value is 0.001 (1 ms).**

### Total time
You can specify the total simulation time with the ```--custom.simulation.duration``` argument.
For example, if you want a duration of ```10```, you would execute:

```
$ java -jar <path-to-jar> --custom.simulation.duration=10
```

**This argument has seconds [s] as unit.**

**The default value is 5.0.**


### Ovito file path
You can specify the path where the Ovito file will be saved with the ```--output.ovito``` argument.
For example, if you want to save the file in the ```/tmp``` directory, you would execute:

```
$ java -jar <path-to-jar> --custom.output.ovito=/tmp/ovito.xtz
```

**There is no default value.**

### Positions file path
You can specify the path where the positions file will be saved with the ```--output.movement``` argument.
For example, if you want to save the file in the ```/tmp``` directory, you would execute:

```
$ java -jar <path-to-jar> --custom.output.movement=/tmp/positions.m
```

**There is no default value.**

**Note:** The positions file is just a MatLab/Octave script that has two intialized array variables: ```x``` and ```y```, both with the same amount of elements (i.e the 'x' and 'y' values for each step of the simulation).



## Authors

- [Juan Marcos Bellini](https://github.com/juanmbellini)
- [Matías Fraga](https://github.com/matifraga)
