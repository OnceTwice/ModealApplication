package com.ff.modealapplication.app.core.service;

import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.util.Base;
import com.ff.modealapplication.app.core.vo.ItemVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by bit-desktop on 2017-02-02.
 */

public class ItemService {

    // 상품 목록 -----------------------------------------------------------------------------------
    public List<Map<String, Object>> itemList(Long shopNo) {

        String url = Base.url + "modeal/list/shopItemList";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);

        // 안드로이드에서 이클립스로 데이터를 보내기
        int responseCode = httpRequest.send("no=" + shopNo).code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }
        // 이클립스에서 안드로이드로 데이터를 받기
        ItemService.JSONResultItemList jsonResult = fromJSON(httpRequest, ItemService.JSONResultItemList.class);

        return jsonResult.getData();
    }

    private class JSONResultItemList extends JSONResult<List<Map<String, Object>>> {
    }

    // 상품 등록 -----------------------------------------------------------------------------------
    public void itemInsert(String item_name, Long ori_price, Long count, Long price, String exp_date, Long discount,Long shopNo, Long itemCategoryNo) {

        // 데이터를 가져올 url를 작성해줌
        String url = Base.url + "modeal/list/itemInsert";    // url(뒤에/list?no=2) 연결 사이트를 이클립스에 프로젝트를 만들어줘야함
        HttpRequest httpRequest = HttpRequest.post(url);                           // post 방식으로 연결
        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);                 // 전달 타입(클래스명.메서드명(파라미터))
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);                      // 받을 타입
        httpRequest.connectTimeout(10000);                                         // 웹에서 응답이 없을때 3초 후 연결 종료
        httpRequest.readTimeout(10000);                                            // 웹으로 요청이 없을때(값이 없을경우) 3초 후 연결 종료

        // from 방식 (안드로이드에서 이클립스(서버)로 데이터를 전송시 사용)
        int responseCode = httpRequest.send(                                      // 키값은 타입(vo)에 맞는 변수명으로 작성하고 밸류값은 내가 받아온 파라미터명을 작성해준다.
                "name=" + item_name +
                        "&oriPrice=" + ori_price +
                        "&shopNo=" + shopNo +
                        "&itemCategoryNo="+itemCategoryNo+
                        "&count=" + count +
                        "&price=" + price +
                        "&expDate=" + exp_date +
                        "&discount=" + discount).code();

        if (responseCode != HttpURLConnection.HTTP_OK) {                        // http_ok : 접속성공 코드번호 : 200
            throw new RuntimeException("HTTP Response : " + responseCode);   // if문 조건식이 참이면 RuntimeException(에러)나고
        }
    }

    private class JSONResultItemInsert extends JSONResult<ItemVo> {
        // ↑ String 에서 원하는 데이터를 추출하기 위해 JSONResult 사용,
        // 데이터(스트링) 안에서 원하는 값을 추출하기 위해 <List<ItemVo>> 사용
    }

    // 상품 수정 - 수정페이지 출력  ----------------------------------------------------------------
    public ItemVo itemModify(Long shopNo) {

        // 데이터를 가져올 url를 작성해줌
        String url = Base.url + "modeal/list/itemModify";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);


        // 안드로이드에서 이클립스로 데이터를 보내기
        int responseCode = httpRequest.send("no=" + shopNo).code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }
        // 이클립스에서 안드로이드로 데이터를 받기
        ItemService.JSONResultItemModify jsonResult = fromJSON(httpRequest, ItemService.JSONResultItemModify.class);

        return jsonResult.getData();
    }

    private class JSONResultItemModify extends JSONResult<ItemVo> {
        // ↑ String 에서 원하는 데이터를 추출하기 위해 JSONResult 사용,
        // 데이터(스트링) 안에서 원하는 값을 추출하기 위해 <List<ItemVo>> 사용
    }

    // 상품 수정 - 업데이트 ------------------------------------------------------------------------
//    public Map<String, Object> itemModify(String item_name, Long ori_price, Long count, Long price, String exp_date, Long discount) {
//
//        // 데이터를 가져올 url를 작성해줌
//        String url = Base.url + "modeal/list/itemModify";
//        HttpRequest httpRequest = HttpRequest.post(url);
//
//        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
//        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
//        httpRequest.connectTimeout(10000);
//        httpRequest.readTimeout(10000);
//
//        // from 방식 (안드로이드에서 이클립스(서버)로 데이터를 전송시 사용)
//        int responseCode = httpRequest.send( // 키값은 타입(vo)에 맞는 변수명으로 작성하고 밸류값은 내가 받아온 파라미터명을 작성해준다.
//                "name=" + item_name +
//                        "&oriPrice=" + ori_price +
//                        "&count=" + count +
//                        "&price=" + price +
//                        "&expDate=" + exp_date +
//                        "&discount=" + discount).code();
//
//        if (responseCode != HttpURLConnection.HTTP_OK) {
//            throw new RuntimeException("HTTP Response : " + responseCode);
//        }
//
//        // 이클립스에서 안드로이드로 데이터를 받기
//        ItemService.JSONResultItemModify jsonResult = fromJSON(httpRequest, ItemService.JSONResultItemModify.class);
//        return jsonResult.getData();
//    }
//
//    private class JSONResultItemModify extends JSONResult<Map<String, Object>> {
//        // ↑ String 에서 원하는 데이터를 추출하기 위해 JSONResult 사용,
//        // 데이터(스트링) 안에서 원하는 값을 추출하기 위해 <List<ItemVo>> 사용
//    }

    // 상품 삭제
    public void itemDelete(String item_name, Long ori_price, Long count, Long price, String exp_date, Long discount) {

        // 데이터를 가져올 url를 작성해줌
        String url = Base.url + "modeal/list/itemDelete";
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);

        // from 방식 (안드로이드에서 이클립스(서버)로 데이터를 전송시 사용)
        int responseCode = httpRequest.send(
                "name=" + item_name +
                        "&oriPrice=" + ori_price +
                        "&count=" + count +
                        "&price=" + price +
                        "&expDate=" + exp_date +
                        "&discount=" + discount).code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }
    }

    private class JSONResultItemDelete extends JSONResult<ItemVo> {
        // ↑ String 에서 원하는 데이터를 추출하기 위해 JSONResult 사용,
        // 데이터(스트링) 안에서 원하는 값을 추출하기 위해 <List<ItemVo>> 사용
    }

    // 상품 상세 목록 ------------------------------------------------------------------------------ (170209/상욱추가)
    public Map<String, Object> itemDetail(Long no) {
        String url = Base.url + "modeal/list/itemDetail";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(httpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);

        httpRequest.send("no=" + no);
        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response :" + responseCode);
        }

        ItemMap itemMap = fromJSON(httpRequest, ItemMap.class);
        Log.w("::::::::::::::::::::::", itemMap.getData() + "");
        return itemMap.getData();
    }

    private class ItemMap extends JSONResult<Map<String, Object>> {
    }

    // 상품 보이기 / 숨기기
    public void itemView(Long no, Long check) {
        String url = "http://192.168.1.93:8088/modeal/list/itemView";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(httpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);

        httpRequest.send("no=" + no + "&check=" + check);
        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response :" + responseCode);
        }
    }

    // JSON 문자열을 자바 객체로 변환 --------------------------------------------------------------
    protected <V> V fromJSON(HttpRequest request, Class<V> target) {
        V v = null;

        try {
            Gson gson = new GsonBuilder().create();         // GSON 인스턴스 생성

            Reader reader = request.bufferedReader();       // http request에서 값을 가져오는 것, reader는 web 상에 /list에 뜬 data
            v = gson.fromJson(reader, target);              // JSONResultUserList 클래스 객체로 변환 = 제이손타입의 리더를 타켓타입으로 변경
            reader.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return v;
    }
}
