package xyz.synse.datacenter.command.commands;

import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;
import xyz.synse.datacenter.command.Command;
import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.service.Service;
import xyz.synse.datacenter.service.services.CamerasService;
import xyz.synse.datacenter.service.services.PNetService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ServicesCommand extends Command {
    public ServicesCommand() {
        super("services");
    }

    @Override
    public boolean runCommand(String commandName, ArgumentsContainer argumentsContainer) {
        if(argumentsContainer.hasExact(1) && argumentsContainer.getArgument(0).getString().equalsIgnoreCase("list")){
            Logger.i("List of running services: ");

            for(Service service : MantleAPI.INSTANCE.servicesManager.getRunningServices().values()){
                Logger.i(service.getClass().getSimpleName() + " : Runtime: " + (service.getRunningTime() / 1_000D) + " s");
            }
            return true;
        }else if (argumentsContainer.hasAtLeast(1) && argumentsContainer.getArgument(0).getString().equalsIgnoreCase("stop")){
            if(argumentsContainer.hasLessThan(2)){
                Logger.i("You need to specify name of the service");
                return true;
            }

            String serviceName = argumentsContainer.getArgument(1).getString();
            Optional<Service> optional = MantleAPI.INSTANCE.servicesManager.getRunningServices().values().stream().filter(service -> service.getClass().getSimpleName().equalsIgnoreCase(serviceName)).findFirst();

            if(optional.isEmpty()){
                Logger.i("No service by name " + serviceName);
                return true;
            }

            MantleAPI.INSTANCE.servicesManager.stopService(optional.get().getClass());
            return true;
        }else if (argumentsContainer.hasAtLeast(1) && argumentsContainer.getArgument(0).getString().equalsIgnoreCase("start")){
            if(argumentsContainer.hasLessThan(2)){
                Logger.i("You need to specify name of the service");
                return true;
            }

            String serviceName = argumentsContainer.getArgument(1).getString();
            Optional<ServiceNameToInstance> optional = Arrays.stream(ServiceNameToInstance.values()).filter(service -> service.isMatch(serviceName)).findFirst();

            if(optional.isEmpty()){
                Logger.i("No service by name " + serviceName);
                return true;
            }

            MantleAPI.INSTANCE.servicesManager.startService(optional.get().supplier.get());
            return true;
        }

        return false;
    }

    @Override
    public List<String> getUsages() {
        return new ArrayList<>() {
            {
                add("services list");
                add("services stop <ServiceName>");
                add("services start <ServiceName>");
            }
        };
    }

    private enum ServiceNameToInstance{
        PNET_SERVICE(() -> new PNetService(4477), "PNetService", "PNet Service"),
        CAMERAS_SERVICE(() -> new CamerasService(30_000), "CamerasService", "Cameras Service");

        public final Supplier<Service> supplier;
        private final List<String> aliases;

        ServiceNameToInstance(Supplier<Service> supplier, String... aliases) {
            this.supplier = supplier;
            this.aliases = List.of(aliases);
        }

        boolean isMatch(String str){
            return str.equalsIgnoreCase(this.name()) || aliases.stream().anyMatch(cmd -> cmd.equalsIgnoreCase(str));
        }
    }
}
