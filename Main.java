public class Main {
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("INFO: Starting application");
			Container container = new Container();
			container.startApplication();
		} catch (Exception ex) {
			System.out.println("ERROR: Error during initialization of application. Please check the config");
			System.out.println(ex.getMessage());
			System.out.println("Press enter to proceed");
			System.in.read();
		}
	}
}
