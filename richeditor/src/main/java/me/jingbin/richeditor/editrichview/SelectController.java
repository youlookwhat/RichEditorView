package me.jingbin.richeditor.editrichview;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 双状态选择控制器，保证其中的选择是互斥的
 * Created by jingbin on 2018/11/12.
 */

@SuppressWarnings("WeakerAccess")
public class SelectController {
    private ArrayList<Long> stateAList;
    private ArrayDeque<Long> stateBList;

    private StatesTransHandler handler;
    private int num;

    public  static SelectController createController(){
        return new SelectController();
    }

    private SelectController(){
        num = 1;
        stateBList = new ArrayDeque<>(num);
        stateAList = new ArrayList<>();
    }

    public SelectController add(long id){
        stateAList.add(id);
        return this;
    }

    public SelectController addAll(Long... id){
        Collections.addAll(stateAList, id);
        return this;
    }

    public SelectController setStateBNum(int num){
        this.num = num;
        return this;
    }

    public void changeState(long id){
        long temp;
        if(stateAList.contains(id)){
            stateAList.remove(id);
            if(num > 0 && stateBList.size() >= num){
                temp = stateBList.poll();
                stateAList.add(temp);
                if(handler != null)
                    handler.handleB2A(temp);
            }
            stateBList.add(id);
            if(handler != null)
                handler.handleA2B(id);
        }else if(stateBList.contains(id)){
            stateBList.remove(id);
            stateAList.add(id);
            if(handler != null)
                handler.handleB2A(id);
        }

    }

    public void reset(){
        moveAll2StateA();
    }

    private void moveAll2StateA(){
        while (!stateBList.isEmpty()){
            long temp = stateBList.poll();
            stateAList.add(temp);
            handler.handleB2A(temp);
        }
    }

    public boolean contain(long id){
        return stateAList.contains(id) || stateBList.contains(id);
    }

    public void setHandler(StatesTransHandler handler) {
        this.handler = handler;
    }


    public interface StatesTransHandler {
        void handleA2B(long id);
        void handleB2A(long id);
    }

    @SuppressWarnings("unused")
    public abstract class StatesTransAdapter implements StatesTransHandler{
        @Override
        public void handleA2B(long id) {
            Log.e("handleA2B",id+"");
        }

        @Override
        public void handleB2A(long id) {
            Log.e("handleB2A",id+"");
        }
    }
}
