import org.model.DBContext;
import org.model.DBHandler;
import org.zkoss.zss.model.impl.BTree;
import org.zkoss.zss.model.impl.CountedBTree;
import org.zkoss.zss.model.impl.statistic.AbstractStatistic;
import org.zkoss.zss.model.impl.statistic.CombinedStatistic;
import org.zkoss.zss.model.impl.statistic.CountStatistic;
import org.zkoss.zss.model.impl.statistic.KeyStatistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class BTreeTest {

    public static void main(String[] args) {
        deepTest();
    }

    public static void deepTest(){
        String url = "jdbc:postgresql://127.0.0.1:5432/ibd";
        String driver = "org.postgresql.Driver";
        String userName = "mangesh";
        String password = "mangesh";
        DBHandler.connectToDB(url, driver, userName, password);
        DBContext dbContext = new DBContext(DBHandler.instance.getConnection());
        CountedBTree btree = new CountedBTree(dbContext, "tNMBC", false);
        testSplitNodeByCount(dbContext, btree);
        //testNodeMergeByCount(dbContext);
        //testSplitNodeSplitParentByCount(dbContext);
        //NodeMergeRootMergeByCount(dbContext);
        //NodeMergeRootMerge1ByCount(dbContext);
        dbContext.getConnection().commit();
        dbContext.getConnection().close();
    }
    public static void simpleTest(){
        String url = "jdbc:postgresql://127.0.0.1:5432/ibd";
        String driver = "org.postgresql.Driver";
        String userName = "mangesh";
        String password = "mangesh";
        DBHandler.connectToDB(url, driver, userName, password);
        DBContext dbContext = new DBContext(DBHandler.instance.getConnection());
        CountedBTree btree = new CountedBTree(dbContext, "Test1", false);
        ArrayList<Integer> arrayList = new ArrayList<>();

        // Random operations
        final int operations=5;
        Random random = new Random(1);
        Random randomOperation = new Random(1);
        int stats_add =0;
        int stats_remove =0;


        System.out.println("Start adding Initial Records");
        // Add initial data points.
        for (int i=0;i<operations;i++)
        {
            stats_add++;
            int randomValue = random.nextInt();
            ArrayList<Integer> insertList = new ArrayList<>();
            insertList.add(randomValue);
            btree.insertIDs(dbContext, i, insertList);
            System.out.println("Records added:" + randomValue);
            dbContext.getConnection().commit();
            arrayList.add(i,randomValue);
        }

        System.out.println("Random operations");
        // Perform RANDOM operations.
        for (int i=0;i<operations;i++)
        {
            int operation = randomOperation.nextInt(2);
            int randomPos = random.nextInt(arrayList.size());
            int randomValue = random.nextInt();

            switch (operation)
            {
                case 0:
                    stats_add++;
                    ArrayList<Integer> randomValueList = new ArrayList<>();
                    randomValueList.add(randomValue);
                    //System.out.println(btree.size(dbContext) + " add " + randomPos + " " + randomValueList);
                    btree.insertIDs(dbContext, randomPos, randomValueList);
                    arrayList.add(randomPos, randomValue);
                    break;
                case 1:
                    stats_remove++;
                    ArrayList<Integer> randomPosList = new ArrayList<>();
                    //System.out.println(btree.size(dbContext) + " remove " + randomPos);
                    btree.deleteIDs(dbContext, randomPos, 1);
                    arrayList.remove(randomPos);
                    break;
            }
            dbContext.getConnection().commit();
        }


        System.out.println("Records added:" + stats_add);
        System.out.println("Records deleated:" + stats_remove);
        System.out.println("BTree size:" + btree.size(dbContext));
        System.out.println("ArrayList size:" + arrayList.size());

        ArrayList<Integer> btree_list = btree.getIDs(dbContext, 0, arrayList.size());
        if (btree.size(dbContext) == arrayList.size()
                && btree_list.equals(arrayList))
            System.out.println("Results Match");
        else
            System.err.println("Results do not match");



        dbContext.getConnection().commit();
        dbContext.getConnection().close();
    }

    public static void testRootInsDelByCount(DBContext context, CountedBTree testTree) {


        ArrayList<Integer> key = new ArrayList<>();
        key.add(1);

        testTree.insertIDs(context, 0, key);
        //Integer test = testTree.lookup(context, key, AbstractStatistic.Type.COUNT);
        testTree.deleteIDs(context, 0, 1);

        testTree.insertIDs(context, 0, key);
        //test = testTree.getByCount(context, 0);
        ArrayList<Integer> key1 = new ArrayList<>();
        testTree.insertIDs(context, 1, key1);
        //test = testTree.getByCount(context, 2);
        testTree.insertIDs(context, 1, key);
        testTree.insertIDs(context, 1, key);


    }

    public static void testRootSplitByCount(DBContext context) {

        int[] a = {5, 25, 50};
        int[] rootids = {0, 0, 0};

        for(int i = 0; i < 3; i++){
            String testName = "testRootSplit"+i;
            CountedBTree testTree = new CountedBTree(context, testName, false);
            ArrayList<Integer> ids = new ArrayList<>();
            ids.add(100);
            ids.add(200);
            ids.add(300);
            ids.add(400);
            ids.add(a[i]*10);
            testTree.insertIDs(context, 0, ids);

        }

    }

    public static void testSplitNodeByCount(DBContext context, CountedBTree testTree) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(50);
        testTree.insertIDs(context, 0, ids);

        ids.set(0, 100);
        testTree.insertIDs(context, 1, ids);

        ids.set(0, 200);
        testTree.insertIDs(context, 2, ids);

        ids.set(0, 250);
        testTree.insertIDs(context, 3, ids);

        ids.set(0, 300);
        testTree.insertIDs(context, 4, ids);

        ids.set(0, 400);
        testTree.insertIDs(context, 5, ids);
        context.getConnection().commit();

        ids.set(0, 500);
        testTree.insertIDs(context, 6, ids);

        /*
        ids.add(600);
        ids.add(700);
        ids.add(800);
        */

        /*
        ArrayList<Integer> new_ids = new ArrayList<>();
        new_ids.add(30);
        testTree.insertIDs(context, 1, new_ids);
        new_ids.remove(0);
        new_ids.add(150);
        testTree.insertIDs(context, 3, new_ids);
        new_ids.remove(0);
        new_ids.add(230);
        testTree.insertIDs(context, 5, new_ids);
        new_ids.remove(0);
        new_ids.add(270);
        testTree.insertIDs(context, 7, new_ids);
        new_ids.remove(0);
        new_ids.add(350);
        testTree.insertIDs(context, 9, new_ids);
        new_ids.remove(0);
        new_ids.add(450);
        testTree.insertIDs(context, 11, new_ids);
        new_ids.remove(0);
        new_ids.add(550);
        testTree.insertIDs(context, 13, new_ids);
        new_ids.remove(0);
        new_ids.add(800);
        testTree.insertIDs(context, 16, new_ids);
        */
    }

    public static void testSplitNodeSplitParentByCount(DBContext context) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(50);
        ids.add(100);
        ids.add(200);
        ids.add(250);
        ids.add(300);
        ids.add(400);
        ids.add(500);
        ids.add(600);
        ids.add(700);
        ids.add(800);

        int[] a = {1, 8, 16};
        int[] aa = {3, 8, 17};
        boolean valid;
        for(int i = 0; i < 3; i++){
            CountedBTree testTree = new CountedBTree(context, "tSNSPBC"+i, false);
            testTree.insertIDs(context, 0, ids);
            ArrayList<Integer> new_ids = new ArrayList<>();
            new_ids.add(30);
            testTree.insertIDs(context, 1, new_ids);
            new_ids.remove(0);
            new_ids.add(150);
            testTree.insertIDs(context, 3, new_ids);
            new_ids.remove(0);
            new_ids.add(230);
            testTree.insertIDs(context, 5, new_ids);
            new_ids.remove(0);
            new_ids.add(270);
            testTree.insertIDs(context, 7, new_ids);
            new_ids.remove(0);
            new_ids.add(350);
            testTree.insertIDs(context, 9, new_ids);
            new_ids.remove(0);
            new_ids.add(450);
            testTree.insertIDs(context, 11, new_ids);
            new_ids.remove(0);
            new_ids.add(550);
            testTree.insertIDs(context, 13, new_ids);
            new_ids.remove(0);
            new_ids.add(800);
            testTree.insertIDs(context, 16, new_ids);
            new_ids.remove(0);
            new_ids.add(a[i]*10);
            testTree.insertIDs(context, aa[i], new_ids);

        }
    }

    public static void testNodeMergeByCount(DBContext context, CountedBTree testTree) {
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            ids.add(i*100+100);
        }
        testTree.insertIDs(context, 0, ids);
        testTree.deleteIDs(context, 2, 1);

    }

    public static void NodeMergeRootMergeByCount(DBContext context, CountedBTree testTree) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(50);
        ids.add(100);
        ids.add(200);
        ids.add(230);
        ids.add(270);
        ids.add(300);
        ids.add(330);
        ids.add(400);
        ids.add(500);
        ids.add(550);
        ids.add(700);
        ids.add(800);
        ids.add(850);


        testTree.insertIDs(context, 0 , ids);
        testTree.deleteIDs(context, 0, 1);

    }

    public static void NodeMergeRootMerge1ByCount(DBContext context, CountedBTree testTree) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(50);
        ids.add(100);
        ids.add(200);
        ids.add(230);
        ids.add(270);
        ids.add(300);
        ids.add(330);
        ids.add(400);
        ids.add(500);
        ids.add(550);
        ids.add(700);
        ids.add(800);
        ids.add(850);
        ids.add(900);
        ids.add(950);
        ids.add(1000);
        ids.add(1050);
        ids.add(1100);
        ids.add(1150);


        testTree.insertIDs(context, 0, ids);
        testTree.deleteIDs(context, 0, 1);
        testTree.deleteIDs(context, 13, 1);
        testTree.deleteIDs(context, 16, 1);


    }


}
