package gr.jhug.picocli;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerHostConfig;
import com.github.dockerjava.api.model.ContainerMount;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ContainerNetworkSettings;
import com.github.dockerjava.api.model.ContainerPort;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_GridThemes;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.ocpsoft.prettytime.PrettyTime;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "ps")
@RegisterForReflection(targets = {
        Container.class,
        ContainerPort.class,
        ContainerMount.class,
        ContainerNetworkSettings.class,
        ContainerHostConfig.class,
        ContainerNetwork.class
})
public class PsCommand implements Runnable {

    @CommandLine.Option(names = {"--all", "-a"}, defaultValue = "false", description = "Whether or not to include stopped containers")
    boolean all;

    private final DockerClient dockerClient;
    private final PrettyTime prettyTime;

    public PsCommand(DockerClient dockerClient, PrettyTime prettyTime) {
        this.dockerClient = dockerClient;
        this.prettyTime = prettyTime;
    }


    @Override
    public void run() {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(all).exec();
        AsciiTable table = new AsciiTable();
        table.getContext().setWidth(200).setGridTheme(TA_GridThemes.NONE);
        table.addRow(Arrays.asList("CONTAINER ID", "IMAGE", "COMMAND", "CREATED", "STATUS", "PORTS", "NAMES"));
        containers.forEach(c -> {
            List<String> data = new ArrayList<>(7);
            data.add(c.getId().substring(0, 12));
            data.add(c.getImage());
            String effectiveCommand = c.getCommand().length() > 20 ? c.getCommand().substring(0, 20) : c.getCommand();
            data.add("\"" + effectiveCommand + "\"");
            data.add(prettyTime.format(new Date(c.getCreated()*1000)));
            data.add(c.getStatus());
            ContainerPort[] ports = c.getPorts();
            List<String> portStrs = new ArrayList<>(ports.length);
            for (ContainerPort port : ports) {
                portStrs.add(port.getIp() + ":" + port.getPublicPort() + "->" + port.getPrivatePort() + "/" + port.getType());
            }
            data.add(String.join(",", portStrs));
            data.add(Arrays.stream(c.getNames()).map(s -> {
                if (s.startsWith("/")) {
                    return s.substring(1);
                }
                return s;
            }).collect(Collectors.joining(",")));
            table.addRow(data);
        });
        System.out.println(table.render());
    }
}
