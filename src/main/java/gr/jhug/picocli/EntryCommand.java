package gr.jhug.picocli;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = { VersionCommand.class, PsCommand.class, RmCommand.class })
public class EntryCommand {
}
