package nirusu.nirubot.command.fun.moasic;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.command.IPrivateCommand;
import nirusu.nirubot.command.PrivateCommandContext;
import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.utility.ParallelMosaiqueEasel;
import nirusu.nirubot.command.fun.moasic.utility.ParallelRectangleArtist;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Mosaic implements IPrivateCommand {
    private static final int TILE_WIDTH = 20;
    private static final int TILE_HEIGHT = 15;

    @Override
    public void execute(PrivateCommandContext ctx) {
        if (ctx.getAttachments().size() != 1) {
            ctx.reply("Softwaretechnik ist die Lehre von der Softwarekonstruktion\\:" +
                    " der systematischen Entwicklung und Pflege von Softwaresystemen");
            return;
        }

        try {
            final File inFile = ctx.getAttachments().get(0).downloadToFile().get();
            BufferedImage input = new BufferedArtImage(ImageIO.read(inFile)).toBufferedImage();

            //load tiles
            var tileFolder = new File("src/main/resources/images");
            List<BufferedArtImage> tiles = new ArrayList<>();
            var files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(tileFolder.listFiles())));
            files.sort(Comparator.comparing(File::getName));
            for (var tile : files) {
                tiles.add(new BufferedArtImage(ImageIO.read(tile)));
            }

            List<BufferedImage> tilesAsI = tiles.stream().map(BufferedArtImage::toBufferedImage).collect(Collectors.toList());
            ParallelRectangleArtist artist = new ParallelRectangleArtist(tilesAsI,TILE_WIDTH, TILE_HEIGHT);
            ParallelMosaiqueEasel easel = new ParallelMosaiqueEasel();
            BufferedImage resultImage = easel.createMosaique(input, artist);

            File result = new File("result.png");
            ImageIO.write(resultImage, "png", result);
            ctx.getChannel().sendFile(result).queue();



        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp("this command creates a mosaic from the given input image", "", this);
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.reply("Softwaretechnik ist die Lehre von der Softwarekonstruktion\\:" +
                " der systematischen Entwicklung und Pflege von Softwaresystemen");
    }

    @Override
    public List<String> alias() {
        return Collections.singletonList("tichy");
    }
}
