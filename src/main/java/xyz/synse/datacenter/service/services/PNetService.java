package xyz.synse.datacenter.service.services;

import nl.pvdberg.pnet.client.Client;
import nl.pvdberg.pnet.event.PNetListener;
import nl.pvdberg.pnet.packet.Packet;
import nl.pvdberg.pnet.packet.PacketReader;
import nl.pvdberg.pnet.server.Server;
import nl.pvdberg.pnet.server.util.PlainServer;
import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.cameras.Camera;
import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.service.Service;
import xyz.synse.datacenter.utils.image.ImageData;
import xyz.synse.datacenter.utils.utilities.BatteryInfo;
import xyz.synse.datacenter.utils.utilities.Orientation;

import java.io.IOException;
import java.net.Socket;

public class PNetService extends Service {
    private final int tcpPort;
    private Server server;

    public PNetService(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    @Override
    public void onStart() {
        try {
            this.server = new PlainServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.server.setListener(new PNetListener() {
            @Override
            public void onConnect(Client c) {
                Logger.i("Client connected. " + c.toString());
            }

            @Override
            public void onDisconnect(Client c) {
                Logger.i("Client disconnected. " + c.toString());

                // Find the camera that disconnected
                for(Camera camera : MantleAPI.INSTANCE.camerasManager.getCameras().values()){
                    if(camera.getConnection() == c.getSocket()){
                        camera.socketDisconnected();
                    }
                }
            }

            @Override
            public void onReceive(Packet p, Client c) {
                if(p.getPacketID() == (short)112) {
                    try {
                        // Reader
                        PacketReader pr = new PacketReader(p);
                        // Camera socket
                        Socket connection = c.getSocket();

                        // Packet data
                        String cameraID = pr.readString();
                        int width = pr.readInt();
                        int height = pr.readInt();

                        // Orientation of the camera and hardware rotation of the camera
                        final Orientation orientation = new Orientation(pr.readFloat(), pr.readFloat(), pr.readFloat());
                        float cameraAngleHardware = pr.readFloat();

                        // Information of the battery. (Level, Charging status, Connection type)
                        BatteryInfo batteryInfo = new BatteryInfo(pr.readInt(), pr.readBoolean(), pr.readString());

                        // Image data, and constructed image data
                        byte[] YUVImageData = pr.readBytes();
                        ImageData imageData = new ImageData(YUVImageData, width, height);

                        // Call calculation
                        MantleAPI.INSTANCE.camerasManager.getCamera(cameraID).onFrame(connection, imageData, orientation, cameraAngleHardware, batteryInfo);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        server.start(tcpPort);
    }

    @Override
    public void onStop() {
        this.server.stop();
    }

    public Server getServer() {
        return server;
    }
}
