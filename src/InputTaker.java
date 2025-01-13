public class InputTaker {

    public static void InputAndSave(){
        Settings settings = Settings.getInstance();

        // Window settings
        System.out.println("Enter the window height: ");
        settings.setSetting("Window", "Height", System.console().readLine());
        System.out.println("Enter the window width: ");
        settings.setSetting("Window", "Width", System.console().readLine());

        // Grid settings
        System.out.println("Enter the grid size: ");
        settings.setSetting("Grid", "Size", System.console().readLine());

        // Farmer
        System.out.println("Enter the initial farmer count: ");
        settings.setSetting("Farmer", "Count", System.console().readLine());
        System.out.println("Enter Plant Increase: ");
        settings.setSetting("Farmer", "Plant_increase", System.console().readLine());
        System.out.println("Enter Repair Increase: ");
        settings.setSetting("Farmer", "Repair_increase", System.console().readLine());

        //Carrot
        System.out.println("Enter Grow Probability: ");
        settings.setSetting("Carrot", "Grow_probability", System.console().readLine());

        // Rabbit
        System.out.println("Enter the initial rabbit count: ");
        settings.setSetting("Rabbit", "Count", System.console().readLine());
        System.out.println("Enter Spawn Probability: ");
        settings.setSetting("Rabbit", "Spawn_probability", System.console().readLine());

        // Thread
        System.out.println("Enter the thread tick interval: ");
        settings.setSetting("Thread", "Tick_interval", System.console().readLine());
    }
}
