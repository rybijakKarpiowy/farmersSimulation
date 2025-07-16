# Farm Simulation
### University project for Object Oriented Programming classes

### Idea
We developed a multi-threaded farm simulation where farmers manage crops while protecting them from rabbits using dogs. The simulation features a complex ecosystem with dynamic interactions between farmers, rabbits, dogs, and growing carrots on a grid-based field.

![App Design](https://github.com/rybijakKarpiowy/farmersSimulation/blob/master/src/images/appDesign.png)

### Details
- Written in Java with advanced concurrency and thread synchronization
- Implements custom thread-safe data structures and locking mechanisms
- Features real-time GUI visualization using Java Swing
- Complex multi-threaded architecture with proper deadlock prevention
- Configurable simulation parameters through settings files

### Core Components

#### Actors
- **Farmers**: Plant carrots, repair damaged tiles, and command dogs to chase rabbits
- **Rabbits**: Eat carrots, move randomly, and spawn dynamically during simulation
- **Dogs**: Chase and eliminate rabbits when commanded by farmers

#### Environment
- **Field**: Grid-based world with thread-safe tile management
- **Tiles**: Individual grid cells that can contain carrots in various growth stages
- **Carrot Growth**: Multi-stage growth system from planted to mature

#### Concurrency Features
- Custom `ExtendedReentrantReadWriteLock` for enhanced thread safety
- Sophisticated locking hierarchy to prevent deadlocks
- Actor-specific mutexes for safe inter-thread communication
- Simulation-wide read/write locks for coordinated access

### Technical Implementation
The simulation uses advanced Java concurrency patterns:
- **Thread Synchronization**: Each actor runs in its own thread with proper synchronization
- **Deadlock Prevention**: Careful lock ordering and consistent locking patterns
- **Race Condition Handling**: Volatile fields and atomic operations where appropriate
- **Resource Management**: Proper lock acquisition and release in all code paths

### Configuration
The system supports extensive configuration through CSV files:
- Grid size and window dimensions
- Actor spawn rates and behavior parameters
- Carrot growth probabilities and farming effectiveness
- Thread timing and synchronization intervals

### Execution
#### Prerequisites
- Java 17 or higher
- Swing GUI support
- Image assets in `src/images/` directory

#### Running the Simulation
1. Compile all Java files
2. Run the Main class:
3. Choose to input custom settings or use defaults
4. Watch the simulation run in real-time

#### Settings Configuration
The simulation reads from `src/settings/default.csv` and can save user preferences to `src/settings/userSettings.csv`. Key configurable parameters include:
- **Window**: Height and width dimensions
- **Grid**: Simulation field size
- **Farmer**: Count, planting efficiency, repair effectiveness
- **Rabbit**: Initial count and spawn probability
- **Carrot**: Growth probability
- **Thread**: Tick interval for simulation speed

### Architecture Highlights
- **Abstract Base Classes**: `ActorAbstract` and `ThreadAbstract` provide common functionality
- **Factory Pattern**: Dynamic actor creation and management
- **Observer Pattern**: GUI updates driven by simulation state changes
- **Strategy Pattern**: Different actor behaviors implemented through inheritance
- **Singleton Pattern**: Settings management and configuration

### Thread Safety Features
The simulation implements several advanced concurrency concepts:
- **Lock Hierarchies**: Consistent ordering to prevent deadlocks
- **Condition Variables**: Proper thread coordination and signaling
- **Atomic Operations**: Safe updates to shared state
- **Memory Visibility**: Proper use of volatile and synchronization

### Performance Considerations
- Efficient neighbor calculation for actor movement
- Optimized view range calculations for actor perception
- Minimal lock contention through careful design
- Scalable architecture supporting variable grid sizes

### Creators
- Jakub Adamski 160291
- Ashley Jurga 160314
