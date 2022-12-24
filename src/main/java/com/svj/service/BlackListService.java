package com.svj.service;

import com.svj.entity.BlackList;
import com.svj.entity.TradeEntry;
import com.svj.exceptionHandling.StockProcessingException;
import com.svj.repository.BlackListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.svj.utilities.AppUtils.getResourceFileAsStringList;
import static com.svj.utilities.JsonParser.jsonToString;

@Service
@Slf4j
public class BlackListService {
    private BlackListRepository blackListRepository;

    private String niftyFilePath;
    private Map<String, String> nifty50Stocks= new HashMap<>();

    // TODO- valid list of stocks should contain its sector too. These will be considered for screening- Could be in addition to nifty50
    @Autowired
    public BlackListService(BlackListRepository blackListRepository,
                            @Value("${nse.data.nifty50}")String niftyFilePath){
        this.blackListRepository= blackListRepository;
        this.niftyFilePath= niftyFilePath;
    }

    @PostConstruct
    public void init(){
        List<String> niftyStocksWithIndex = getResourceFileAsStringList(niftyFilePath);
        niftyStocksWithIndex.stream().forEach(line-> {
            String stockName = line.split(",")[0];
            String indexName = line.split(",")[1];
            nifty50Stocks.put(stockName, indexName);
        });
    }

    public Set<String> getBlackListedStocks(String traderName){
        log.info("BlackListService: getBlackListedStocks Starting method with traderName: {}", traderName);
        BlackList blackList = blackListRepository.findByTraderName(traderName);
        log.info("BlackListService: getBlackListedStocks Response from DB is : {}",  jsonToString(blackList));
        log.info("BlackListService: getBlackListedStocks Ending method execution");
        if(blackList!= null)
            return blackList.getBlackListedStocks();
        else
            return null;
    }

    public BlackList blackListStock(TradeEntry tradeEntry) {
        if(tradeEntry.getProfit()== null)
            return null;
        if(nifty50Stocks.get(tradeEntry.getSymbol())== null)
            throw new StockProcessingException("Traded stock is not present in the given list");
        BlackList blackList = blackListRepository.findByTraderName(tradeEntry.getTraderName());
        if(tradeEntry.getProfit()< 0){
            // Add to blacklist
            if(blackList== null){
                blackList= BlackList.builder().blackListedStocks(new HashSet<>(Arrays.asList(tradeEntry.getSymbol()))).traderName(tradeEntry.getTraderName()).build();
            }else{
                blackList.getBlackListedStocks().add(tradeEntry.getSymbol());
            }
        }else{
            // remove from blacklist
            if(blackList!= null){
                blackList.getBlackListedStocks().remove(tradeEntry.getSymbol());
            }
        }
        BlackList savedEntry = null;
        if(blackList!= null)
            savedEntry = blackListRepository.save(blackList);
        return savedEntry;
    }
}
