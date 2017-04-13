/*
 Copyright (c) 2015 SONATA-NFV, NEC
 ALL RIGHTS RESERVED.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Neither the name of the SONATA-NFV, NEC
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written
 permission.

 This work has been performed in the framework of the SONATA project,
 funded by the European Commission under Grant number 671517 through
 the Horizon 2020 and 5G-PPP programmes. The authors would like to
 acknowledge the contributions of their colleagues of the SONATA
 partner consortium (www.sonata-nfv.eu).
 */
package eu.sonata.nfv.nec.resolver;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.sonata.nfv.nec.resolver.configuration.ConfigurationModule;
import eu.sonata.nfv.nec.resolver.core.Application;
import eu.sonata.nfv.nec.resolver.core.ApplicationModule;
import eu.sonata.nfv.nec.resolver.core.CliParameters;
import eu.sonata.nfv.nec.resolver.core.GuiceInjectedActor;
import eu.sonata.nfv.nec.resolver.download.DownloadActor;
import eu.sonata.nfv.nec.resolver.eventBus.EventBusModule;
import eu.sonata.nfv.nec.resolver.store.StoreModule;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The main class starts the actual applications. To
 * this end, it handles command line options,
 * bootstraps the Guice modules, starts the Akka
 * actor system, and starts the application using
 * Guice.
 *
 * @author Michael Bredel
 */
public class Main {

    /**
     * Move it, move it, move it.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {

        // Parse the command line options.
        parseCliOptions(args);

        // Initialize Guice.
        Injector injector = initGuice();

        // Start the Akka actor system.
        initAkka(injector);

        // Start the app.
        injector.getInstance(Application.class)
                .start();
    }

    /**
     * Bootstrap the various Guice modules.
     */
    private static Injector initGuice() {
        return Guice.createInjector(
                new ApplicationModule(),
                new ConfigurationModule(),
                new StoreModule(),
                new EventBusModule()
        );
    }

    /**
     * Bootstrap the Akka actor system.
     */
    private static void initAkka(Injector injector) {
        // Create and start the health check actors.
        ActorSystem healthChecks = ActorSystem.create("HealthChecks");
        //system.actorOf(Props.create(GuiceInjectedActor.class, injector, HealthCheckSupervisor.class));
        //system.actorOf(Props.create(GuiceInjectedActor.class, injector, DatabaseCheck.class));

        // Create and start the descriptor handling actors.
        ActorSystem descriptorHandler = ActorSystem.create("DescriptorHandler");
        descriptorHandler.actorOf(Props.create(GuiceInjectedActor.class, injector, DownloadActor.class));
    }

    /**
     * Creates the command line options for the
     * program.
     *
     * @return An Options object containing all the command line options of the program.
     */
    private static Options createCliOptions() {
        // A helper option.
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("Give this help list.")
                .build();
        // The configuration option.
        Option config = Option.builder("c")
                .longOpt("config")
                .desc("The URI to the configuration.")
                .hasArg()
                .build();
        // The username option
        Option username = Option.builder("u")
                .longOpt("user")
                .desc("The user name to access a remote configuration.")
                .hasArg()
                .build();
        // The username option
        Option password = Option.builder("p")
                .longOpt("password")
                .desc("The password to access a remote configuration.")
                .hasArg()
                .build();

        // Create options.
        Options options = new Options();
        options.addOption(help);
        options.addOption(config);
        options.addOption(username);
        options.addOption(password);

        // Return options.
        return options;
    }

    /**
     * Parses the command line arguments.
     *
     * @param args The command line arguments.
     */
    private static void parseCliOptions(String[] args) {
        // Command line options.
        Options options = createCliOptions();
        // Command line parser.
        CommandLineParser parser = new DefaultParser();

        try {
            // Parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption('h')) {
                printHelp(options);
                System.exit(0);
            }
            if (line.hasOption('c')) {
                CliParameters.getInstance().setUri(line.getOptionValue('c'));
            }
            if (line.hasOption('u')) {
                CliParameters.getInstance().setUsername(line.getOptionValue('u'));
            }
            if (line.hasOption('p')) {
                CliParameters.getInstance().setPassword(line.getOptionValue('p'));
            }
        } catch (MissingOptionException | MissingArgumentException e) {
            System.err.println("ERROR: " + e.getMessage() + "\n");
            printHelp(options);
            System.exit(1);
        } catch (ParseException e) {
            // Oops, something went wrong
            System.err.println("ERROR: Parsing CLI parameters failed. Reason: " + e.getMessage() + "\n");
            printHelp(options);
            System.exit(1);
        }
    }

    /**
     * Prints the help of the command.
     *
     * @param options The command's options.
     */
    private static void printHelp(Options options) {
        // A help formatter.
        HelpFormatter formatter = new HelpFormatter();
        // Print help.
        formatter.printHelp("son-resolver [OPTIONS]", options);
    }
}
