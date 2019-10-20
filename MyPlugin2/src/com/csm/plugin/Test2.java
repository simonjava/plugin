package com.csm.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.*;

public class Test2 {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/chengsimin/Downloads/单音旋律.mid");
        //File file = new File("/Users/chengsimin/Downloads/伴奏轨.mid");
        System.out.println(file.getPath());
        Sequence sequence = MidiSystem.getSequence(file);
        // Create a sequencer for the sequence
//        Sequencer sequencer = MidiSystem.getSequencer();
//        sequencer.open();
//        sequencer.setSequence(sequence);
//
//        // Start playing
//        sequencer.start();
//        if(true){
//            return;
//        }
        ArrayList<ScoreItem> scoreItems = new ArrayList<>();

        ScoreItem cur =null;
        int trackNumber = 0;
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1(); //表示音符,从0~127代表不同音高
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2(); // 代表音量 多大的音道按下 0 几乎听不道 100算是差不多
                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        if(cur!=null){
                            cur.endTs = event.getTick();
                            scoreItems.add(cur);
                            cur = null;
                        }
                        cur = new ScoreItem(event.getTick(),0,key);
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        if(cur!=null && cur.key == key){
                            cur.endTs = event.getTick();
                            scoreItems.add(cur);
                            cur = null;
                        }
                    } else {
                        System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    System.out.println("Other message: " + message.getClass());
                }
            }
            System.out.println();
        }

        for (ScoreItem scoreItem : scoreItems) {
            System.out.println(scoreItem);
        }
        writeMelpFile(file, scoreItems);
    }

    private static void writeMelpFile(File file, ArrayList<ScoreItem> scoreItems) {
        StringBuilder sb = new StringBuilder();
        if(scoreItems.isEmpty()){
           return;
        }
        sb.append("1 ").append(scoreItems.size()).append("\n");
        for(ScoreItem scoreItem :scoreItems){
            sb.append(scoreItem.beginTs).append(" ").append(scoreItem.endTs).append(" ").append(scoreItem.key%12).append("\n");
        }
        FileWriter writer = null;
        try {
            String fn = file.getName();
            fn = fn.substring(0,fn.indexOf("."));
            writer = new FileWriter(new File(file.getParentFile(),"score_"+fn+".melp2"));
            writer.write(sb.toString());
            System.out.println("写入成功"+file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class ScoreItem {
        long beginTs;
        long endTs;
        int key;

        public long getBeginTs() {
            return beginTs;
        }

        public void setBeginTs(long beginTs) {
            this.beginTs = beginTs;
        }

        public long getEndTs() {
            return endTs;
        }

        public void setEndTs(long endTs) {
            this.endTs = endTs;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public ScoreItem(long beginTs, long endTs, int key) {
            this.beginTs = beginTs;
            this.endTs = endTs;
            this.key = key;
        }

        @Override
        public String toString() {
            return "ScoreItem{" +
                    "beginTs=" + beginTs +
                    ", endTs=" + endTs +
                    ", key=" + key +
                    '}';
        }
    }
}