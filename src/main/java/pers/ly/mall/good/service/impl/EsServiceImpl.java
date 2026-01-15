package pers.ly.mall.good.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.entity.result.PageResult;
import pers.ly.mall.common.exception.EsIOException;
import pers.ly.mall.good.doc.GoodDoc;
import pers.ly.mall.good.dto.SearchGoodDTO;
import pers.ly.mall.good.service.EsService;
import pers.ly.mall.good.vo.SearchGoodVO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class EsServiceImpl implements EsService {
    private final RestHighLevelClient restHighLevelClient;

    public EsServiceImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 添加商品文档
     * @param goodDoc 商品信息
     */
    @Override
    public void addGoodDoc(GoodDoc goodDoc) {
        IndexRequest indexRequest = new IndexRequest("good").id(goodDoc.getId().toString());
        indexRequest.source(JSONUtil.toJsonStr(goodDoc), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsIOException(ErrorConstant.ES_IO_ERROR);
        }
    }

    /**
     * es分页查询
     * @param searchGoodDTO 查寻条件
     * @return 查询结果(list+total)
     */
    @Override
    public PageResult page(SearchGoodDTO searchGoodDTO) {
        SearchRequest searchRequest = new SearchRequest("good");
        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(searchGoodDTO);

        if(searchGoodDTO.getFrom() - 1 < 0 || searchGoodDTO.getSize() < 0) {
            throw new EsIOException(ErrorConstant.PAGE_OR_SIZE_ILLEGAL);
        }
        searchRequest.source()
                .query(boolQueryBuilder)
                .highlighter(new HighlightBuilder()
                        .field("name")
                        .requireFieldMatch(false))
                .from((searchGoodDTO.getFrom() - 1) * searchGoodDTO.getSize())
                .size(searchGoodDTO.getSize());
        //匹配排序
        if(StrUtil.isNotEmpty(searchGoodDTO.getOrderBy()) && searchGoodDTO.getOrderRule() != null) {
            searchRequest.source().sort(searchGoodDTO.getOrderBy(), searchGoodDTO.getOrderRule().equals(SearchGoodDTO.DESC) ? SortOrder.DESC : SortOrder.ASC);
        }

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //处理结果
            SearchHits hits = searchResponse.getHits();
            Long total = Objects.requireNonNull(hits.getTotalHits()).value;
            SearchHit[] searchHits = hits.getHits();
            if(ArrayUtil.isEmpty(searchHits)) {
                return null;
            }
            List<SearchGoodVO> list = Arrays.stream(searchHits).map(
                    searchHit -> {
                        //将数据反序列化
                        String json = searchHit.getSourceAsString();
                        GoodDoc goodDoc = JSONUtil.toBean(json, GoodDoc.class);
                        //替换高亮字段
                        Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                        if(highlightFields != null && !highlightFields.isEmpty()) {
                            HighlightField nameField = highlightFields.get("name");//只对于name字段高亮
                            if(!Objects.isNull(nameField)) {
                                String name = nameField.getFragments()[0].toString();
                                goodDoc.setName(name);
                            }
                        }
                        SearchGoodVO searchGoodVO = new SearchGoodVO();
                        BeanUtils.copyProperties(goodDoc, searchGoodVO);
                        return searchGoodVO;
                    }
            ).toList();

            PageResult result = new PageResult();
            result.setTotal(total);
            result.setRecords(list);
            return result;
        } catch (IOException e) {
            throw new EsIOException(ErrorConstant.ES_IO_ERROR);
        }
    }

    /**
     * 自动补全
     * @param query 查询条件
     * @return 返回补全建议
     */
    @Override
    public List<String> suggest(String query) {
        SearchRequest request = new SearchRequest("good");
        request.source()
                .query(QueryBuilders.termQuery("isOnSale", true)) //过滤出上架的
                .suggest(
                        new SuggestBuilder().addSuggestion("mySuggestion",
                                SuggestBuilders
                                        .completionSuggestion("suggestion")
                                        .prefix(query)
                                        .skipDuplicates(true) //跳过重复
                                        .size(10)
                        )
                );
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestion = suggest.getSuggestion("mySuggestion");
            return suggestion.getOptions()
                    .stream()
                    .map(
                            option -> option.getText().toString()
                    )
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建bool查询
     * @param searchGoodDTO 查询条件
     * @return 构建结果
     */
    private static BoolQueryBuilder buildBoolQuery(SearchGoodDTO searchGoodDTO) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //对于名字的匹配
        if(StrUtil.isEmpty(searchGoodDTO.getSearch())) {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        else {
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchGoodDTO.getSearch()));
        }
        //一定在售
        boolQueryBuilder.filter(QueryBuilders.termQuery("isOnSale", true));
        //匹配价格
        if(searchGoodDTO.getHighPrice() != null && searchGoodDTO.getLowPrice() != null) {
            boolQueryBuilder.filter(
                    QueryBuilders.rangeQuery("price")
                            .gte(searchGoodDTO.getLowPrice())
                            .lte(searchGoodDTO.getHighPrice())
            );
        }
        if(searchGoodDTO.getCategoryIds() != null && !searchGoodDTO.getCategoryIds().isEmpty()) {
            boolQueryBuilder.filter(
                    QueryBuilders.termsQuery("categoryIds", searchGoodDTO.getCategoryIds())
            );
        }
        return boolQueryBuilder;
    }
}
