public class Main {
    static void printUsage() {
        System.out.println("Usage: java Main <field_size> <farmer_count>");
        System.out.println("Flags: -c <carrot_growth_probability> -r <rabbit_spawn_probability> --continue");
    }

    public static void main(String[] args) {
        // parse arguments
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        int fieldSize = Integer.parseInt(args[0]);
        int farmerCount = Integer.parseInt(args[1]);

        double carrotGrowthProbability = 0.6;
        double rabbitSpawnProbability = 0.1;
        boolean shouldContinue = false;

        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-c")) {
                try {
                    carrotGrowthProbability = Double.parseDouble(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid carrot growth probability");
                    printUsage();
                    System.exit(1);
                }
                i++;
            } else if (args[i].equals("-r")) {
                try {
                    rabbitSpawnProbability = Double.parseDouble(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid rabbit spawn probability");
                    printUsage();
                    System.exit(1);
                }
                i++;
            } else if (args[i].equals("--continue")) {
                shouldContinue = true;
            }
        }

        // create simulation
        SimulationManager simulationManager = new SimulationManager(fieldSize, farmerCount, carrotGrowthProbability, rabbitSpawnProbability, shouldContinue);
        simulationManager.run();
    }
}