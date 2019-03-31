public class CacheSimLoveland {
    static int MM[] = new int[2048];
    static int cache[][] = new int[0x10][0x19];
    static String cacheHead[] = new String[]{"slot ", "dirty ", "valid ", "tag ", "data"};
    static int[] addresses = new int[]{0x005, 0x006, 0x007, 0x14c, 0x14d, 0x14e, 0x14f, 0x150, 0x151, 0x3a6, 0x4c3, 0x000, 0x14c, 0x63b, 0x582, 0x000, 0x348, 0x03f, 0x000, 0x14b, 0x14c, 0x63f, 0x083, 0x000};
    static int a = 0;
    static int[] writeArray = {0x99, 0x7};
    static int w = 0;
    static char[] directionsArray = new char[]{'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'D', 'W', 'W', 'R', 'D', 'R', 'R', 'D', 'R', 'R', 'R', 'R', 'D'};
    static int savedBlockStart[] = new int[3];
    static int s = 0;
    static int sbs = 0;
    static int d = 0;
    static int slot = 0;
    static int offset = 0;
    static int tag = 0;
    static int blockStart = 0;
    static int j;
    static int dirty = 0;
    static int valid = 0;
    static String hitOrMiss = "miss";



    public static void main(String[] args) {

        fillMainMemory();

        for (a = 0; a < directionsArray.length; a++) {
            runProgram();
        }
    }


    public static void fillMainMemory() {
        int mmFillWholeList = 0x00;
        //This part fills in 0-ff 7 times in MM*************************************
        for (int repeatedFill = 0; repeatedFill <= 0x7; repeatedFill++) {
            for (int i = 0x00; i <= 0xff; i++) {//if I make this 0x7ff it will show the address of the whole thing in hex******
                MM[mmFillWholeList] = i;
                mmFillWholeList++;
            }
        }
    }


    public static void runProgram() {
        switch (directionsArray[d]) {
            case 'R':
                read();
                break;

            case 'D':
                displayCache();
                break;

            case 'W':
                read();
                write();
                break;
        }
        d++;
    }


    public static void read() {
        tag = (addresses[a] & 0xf00) >>> 8;
        slot = (addresses[a] & 0x0f0) >>> 4;
        offset = (addresses[a] & 0x00f);
        blockStart = (addresses[a] & 0xff0);
        if (cache[slot][2] == 1) {//if valid bit is one
            if (cache[slot][3] == tag) {
                hitOrMiss = "hit";
                displayInformation();
            } else {//if (cache[slot][3] != tag) {
                hitOrMiss = "miss";
                if (cache[slot][1] == 1) {//if dirty bit is 1

                  copyDirtyCacheToMMBlock();
                    cache[slot][1] = 0;

                }
                copyMMBlockToCache();
                cache[slot][3] = tag;//set tag to new value

                displayInformation();

            }
        } else {
            tag = (addresses[a] & 0xf00) >>> 8;
            slot = (addresses[a] & 0x0f0) >>> 4;
            offset = (addresses[a] & 0x00f);
            blockStart = (addresses[a] & 0xff0);
            hitOrMiss = "miss";
            copyMMBlockToCache();
            cache[slot][2] = 1;//valid bit to 1
            cache[slot][3] = tag;//set tag to new value
            displayInformation();
        }
    }


    public static void write() {
        cache[slot][offset + 4] = writeArray[w];
        cache[slot][1] = 1;
        System.out.print("Wrote the value ");
        System.out.printf("%x", writeArray[w]);
        System.out.print(" to address ");
        System.out.printf("%x", addresses[a]);
        savedBlockStart[s] = addresses[a];
        System.out.println();
        System.out.println();
        w++;
        cache[slot][0]=slot;
        cache[slot][2]= 1;
        System.out.print("This is what is is saved Block Start");
        System.out.printf("%x", savedBlockStart[s]);
        s++;
    }

    public static void displayCache() {
        printHeader();
        printCache();
    }


    public static void printHeader() {
        for (int h = 0; h <= 4; h++) {
            System.out.print(cacheHead[h]);
            System.out.print("\t");
        }
        System.out.println();
    }


    public static void printCache() {
        cache[slot][0] = slot;
        cache[slot][1] = dirty;
        cache[slot][2] = valid;
        cache[slot][3] = tag;


        for (int i = 0x0; i <= 0xf; i++) {
            for (int j = 0x0; j <= 0x13; j++) {
                System.out.printf("%x", cache[i][j]);
                System.out.print("\t");
                System.out.print("\t");
            }
            System.out.println();
            System.out.println();
        }
    }


    public static void displayInformation() {
        System.out.println();
        System.out.print("The byte at address ");
        System.out.printf("%x", addresses[a]);
        System.out.print(" is the value ");
        System.out.printf("%x", cache[slot][offset + 4]);
        System.out.println(" (Cache " + hitOrMiss + ")");
        System.out.println();
    }


    public static void copyDirtyCacheToMMBlock() {
        //sbs=0;
       tag = (savedBlockStart[sbs] & 0xf00) >>> 8;//////////////this is the prob
       slot = (savedBlockStart[sbs] & 0x0f0) >>> 4;
        offset = (savedBlockStart[sbs] & 0x00f);
        blockStart = (savedBlockStart[sbs] & 0xff0);//this needs to be the address that was written, not the "current address"
        System.out.print("cache to memory ");
        System.out.print("printing 14c: ");
        System.out.printf("%x", MM[0x14c]);
       // System.out.printf("%x", cache[slot][j]);
        for (int c = 4; c < 0x14; c++) {
            //cache[slot][j] = MM[blockStart];//this does exactly the same as copy mmblock to cache need to change this//////////////////////////////////////////////////
            MM[blockStart]= cache[slot][c];
            System.out.print("blockstart inside:");


           // System.out.printf("%x", MM[blockStart]);
            //System.out.println();
           System.out.printf("%x", cache[slot][c]);
            System.out.println();
            System.out.print(" prints to: ");
            System.out.printf("%x", MM[savedBlockStart[sbs]& 0xff0]);
            System.out.println();
            blockStart++;
            //sbs ++;


            //System.out.println("");
            //System.out.println(blockStart);

        }
       System.out.println("");
        System.out.print("printing 14c: ");
        System.out.printf("%x", MM[0x14c]);
        System.out.println("saved block start: ");
        System.out.println("");
        System.out.printf("%x", savedBlockStart[sbs]);
        System.out.print("sbs is: ");
        System.out.println(sbs);
        System.out.println();
        System.out.print("blockStart outside:");
        blockStart=savedBlockStart[sbs];
        System.out.print("blockstart:");
        System.out.printf("%x", blockStart);
        System.out.println();
        System.out.println("");
        //tag= cache[];
        System.out.print("tag:");
        System.out.printf("%x", tag);
        System.out.println();
        System.out.print("offset:");
        System.out.printf("%x", offset);
        System.out.println();
        System.out.println("");
        System.out.println("saved block start is: ");
        System.out.printf("%x", savedBlockStart[sbs]& 0xff0);
        System.out.println();
        System.out.print("slot is: ");
        System.out.printf("%x", slot);
        System.out.println();
        sbs=1;
        System.out.print("sbs is: ");
        //System.out.println(sbs);
    }

    public static void copyMMBlockToCache() {
        System.out.println();
        System.out.println("memory to cache");
        tag = (addresses[a] & 0xf00) >>> 8;
        slot = (addresses[a] & 0x0f0) >>> 4;
        offset = (addresses[a] & 0x00f);
        blockStart = (addresses[a] & 0xff0);
         for (j = 4; j < 0x14; j++) {
            cache[slot][j] = MM[blockStart];
            System.out.printf("%x", cache[slot][j]);
            blockStart++;
            valid =1;
            cache[slot][0]=slot;
            cache[slot][2]= valid;
            //System.out.println("");
        }

    }
}


