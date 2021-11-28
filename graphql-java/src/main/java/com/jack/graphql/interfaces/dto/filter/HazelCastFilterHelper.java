package com.jack.graphql.interfaces.dto.filter;

import com.google.common.collect.Maps;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.predicates.*;
import com.jack.graphql.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

public class HazelCastFilterHelper {

    public static final Map<FilterType, Class> FACTORY_MAP = Maps.newHashMap();

    static {
        FACTORY_MAP.put(FilterType.Between, BetweenFilter.class) ;
        FACTORY_MAP.put(FilterType.Equal, EqualFilter.class) ;
        FACTORY_MAP.put(FilterType.GreatThan, GreatThanFilter.class) ;
        FACTORY_MAP.put(FilterType.In, InFilter.class) ;
        FACTORY_MAP.put(FilterType.LessThan, LessThanFilter.class) ;
        FACTORY_MAP.put(FilterType.Like, LikeFilter.class) ;
        FACTORY_MAP.put(FilterType.Logic, LogicFilter.class) ;
    }


    public static Predicate toHazelCastPredicate(Filter filter){
        Predicate predicate = null;
        DataType dataType = null;

        switch (filter.filterType){
            case Between:
                BetweenFilter bf = (BetweenFilter) filter;
                dataType = bf.getDataType();
                predicate = new BetweenPredicate(bf.getField()
                    , DataType.fromString(dataType, bf.getLeft())
                    , DataType.fromString(dataType, bf.getRight()));
                break;
            case Equal:
                EqualFilter ef = (EqualFilter)filter;
                dataType = ef.getDataType();
                predicate = new EqualPredicate(ef.getField(), DataType.fromString(dataType, ef.getValue()));
                if (ef.isNot()){
                    predicate = new NotPredicate(predicate);
                }
                break;
            case GreatThan:
                GreatThanFilter gf = (GreatThanFilter)filter;
                dataType = gf.getDataType();
                predicate = new GreaterLessPredicate(gf.getField(), DataType.fromString(dataType,gf.getValue()), gf.isEqual(), false);
                break;
            case In:
                InFilter inF = (InFilter)filter;
                dataType = inF.getDataType();
                predicate = new InPredicate(inF.getField(), DataType.fromString(dataType, inF.getValues()));
                if (inF.isNot()){
                    predicate = new NotPredicate(predicate);
                }
                break;
            case LessThan:
                LessThanFilter lf = (LessThanFilter) filter;
                dataType = lf.getDataType();
                predicate = new GreaterLessPredicate(lf.getField(), DataType.fromString(dataType, lf.getValue()), lf.isEqual(), true);
                break;
            case Like:
                LikeFilter likF = (LikeFilter)filter;
                predicate = new LikePredicate(likF.getField(), likF.getExpression());
                if (likF.isNot()){
                    predicate = new NotPredicate(predicate);
                }
                break;
            case Logic:
                LogicFilter logicFilter = (LogicFilter)filter;
                if (logicFilter.getLogic() == LogicFilter.BooleanLogic.AND){
                    List<Filter> subFilter = logicFilter.getSub();
                    if (CollectionUtils.isEmpty(subFilter)){
                        predicate = new AndPredicate(new TruePredicate());
                    }else {
                        int length = subFilter.size();
                        Predicate[] subPredicates = new Predicate[length];
                        for(int i =0; i< subFilter.size(); i++){
                            subPredicates[i] = toHazelCastPredicate(subFilter.get(i));
                        }
                        predicate = new AndPredicate(subPredicates);
                    }
                }else {
                    List<Filter> subFilter = logicFilter.getSub();
                    if (CollectionUtils.isEmpty(subFilter)){
                        predicate = new OrPredicate(new TruePredicate());
                    }else {
                        int length = subFilter.size();
                        Predicate[] subPredicates = new Predicate[length];
                        for(int i =0; i< subFilter.size(); i++){
                            subPredicates[i] = toHazelCastPredicate(subFilter.get(i));
                        }
                        predicate = new OrPredicate(subPredicates);
                    }
                }
                break;
            default:
                break;
        }

        return predicate;
    }

}
