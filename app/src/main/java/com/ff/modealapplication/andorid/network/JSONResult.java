package com.ff.modealapplication.andorid.network;

/**
 * Created by BIT on 2017-01-19.
 */

public abstract class JSONResult<DataT> {

    private String result;
    private String message;
    private DataT data;

    public JSONResult(String result, String message, DataT data){
        this.result=result;
        this.message=message;
        this.data=data;
    }
    public JSONResult(){
    }
    public boolean isSuccess(){
        return "success".equals(this.result)?true:false;
    }

    @Override
    public String toString() {
        return "JSONResult{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DataT getData() {
        return data;
    }

    public void setData(DataT data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
