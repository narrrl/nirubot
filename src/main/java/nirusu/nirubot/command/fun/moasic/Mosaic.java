package nirusu.nirubot.command.fun.moasic;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Message.Attachment;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.command.PrivateCommandContext;
import nirusu.nirubot.command.ICommandContext;
import nirusu.nirubot.command.IPrivateCommand;
import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.utility.ParallelMosaiqueEasel;
import nirusu.nirubot.command.fun.moasic.utility.ParallelRectangleArtist;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Objects;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Mosaic implements IPrivateCommand {

    private void makeMosaic(int tileWidth, int tileHeight, ICommandContext ctx) {
        if (ctx.getAttachments().size() != 1) {
            ctx.reply("Softwaretechnik ist die Lehre von der Softwarekonstruktion\\:" +
                    " der systematischen Entwicklung und Pflege von Softwaresystemen");
            return;
        }
        // tmp folder to store data
        // tmp/mosaic/{authorId}
        File tmpDir = new File(Nirubot.getTmpDirectory().getAbsolutePath()
                .concat(File.separator + "mosaic" + File.separator + ctx.getAuthor().getId()));
        tmpDir.mkdirs();
        // tmp/mosaic/{authorId}/result.png
        File result = new File(tmpDir.getAbsolutePath().concat(File.separator + "result.png"));

        // if author is already creating a mosaic
        if (result.exists()) {
            ctx.reply("You are already making a mosaic!");
            return;
        }

        Attachment a = ctx.getAttachments().get(0);

        if (!a.isImage()) return;

        String fileName = a.getFileName();

        final File inFile = new File(tmpDir.getAbsolutePath().concat(File.separator + fileName));
        a.downloadToFile(inFile).thenAccept( file -> {
            BufferedImage input;
            try {
                input = new BufferedArtImage(ImageIO.read(file))
                    .toBufferedImage();
            } catch (IOException e) {
                return;
            }
            var tileFolder = new File("src/main/resources/images");
            List<BufferedArtImage> tiles = new ArrayList<>();
            var files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(tileFolder.listFiles())));
            files.sort(Comparator.comparing(File::getName));
            for (var tile : files) {
                try {
                    tiles.add(new BufferedArtImage(ImageIO.read(tile)));
                } catch (IOException e) {
                    ctx.reply("Mosaic failed!");
                    return;
                }
            }

            List<BufferedImage> tilesAsI = tiles.stream().map(BufferedArtImage::toBufferedImage)
                .collect(Collectors.toList());
            ParallelRectangleArtist artist = new ParallelRectangleArtist(tilesAsI,tileWidth, tileHeight);
            ParallelMosaiqueEasel easel = new ParallelMosaiqueEasel();
            BufferedImage resultImage = easel.createMosaique(input, artist);
            try {
                ImageIO.write(resultImage, "png", result);
            } catch (IOException e) {
                ctx.reply("Mosaic failed!");
                return;
            }
            ctx.getChannel().sendFile(result).complete();
        }).thenAccept(end -> {
            tryDelete(result);
            tryDelete(inFile);
        });
    }

    @Override
    public void execute(PrivateCommandContext ctx) {
        //TODO implement a user input tileWidth/tilHeight
        int tileWidth = 20;
        int tileHeight = 15;
        makeMosaic(tileWidth, tileHeight, ctx);
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp("this command creates a mosaic from the given input image", "", this);
    }

    @Override
    public void execute(CommandContext ctx) {
        //TODO implement a user input tileWidth/tilHeight
        int tileWidth = 20;
        int tileHeight = 15;
        makeMosaic(tileWidth, tileHeight, ctx);
    }

    @Override
    public List<String> alias() {
        return Collections.singletonList("tichy");
    }

    private void tryDelete(final File f) {
        if (!f.delete()) {
            // inform that a file couldn't be deleted
            Nirubot.warning(String.format("Couldn't delete file %s, dumping work to another thread",
                f.getAbsolutePath()));
            // dump into thread
            new Thread() {
                @Override
                public void run() {
                    boolean isRunning = true;
                    while(isRunning && f.exists()) {
                        if (!f.exists() && f.delete()) {
                            Nirubot.warning(String.format("Deleted file %s successfully",
                                f.getAbsolutePath()));
                        }
                        if (f.exists()) {
                            try {
                                sleep(5000L);
                            } catch (InterruptedException e) {
                                Nirubot.warning(String.format("Couldn't delete file %s",
                                            f.getAbsolutePath()));
                                isRunning = false;
                            }

                        }
                    }
                }
            }.start();
        }

    }
}
