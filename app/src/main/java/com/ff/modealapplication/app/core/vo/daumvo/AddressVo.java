package com.ff.modealapplication.app.core.vo.daumvo;

public class AddressVo {
    private ChannelVo channel;

    public ChannelVo getChannel() {
        return channel;
    }

    public void setChannel(ChannelVo channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "JSONAddress{" + "channel=" + channel + '}';
    }
}
