package vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy;

import vincemann.github.generic.crud.lib.controller.exception.IdTransformingException;

public class LongUrlParamIdFetchingStrategy extends UrlParamIdFetchingStrategy<Long> {
    public LongUrlParamIdFetchingStrategy(String idUrlParamKey) {
        super(idUrlParamKey);
    }

    @Override
    protected Long transformToIdType(String id) throws IdTransformingException {
        try {
            return Long.parseLong(id);
        }catch (NumberFormatException e){
            throw new IdTransformingException(e);
        }
    }
}