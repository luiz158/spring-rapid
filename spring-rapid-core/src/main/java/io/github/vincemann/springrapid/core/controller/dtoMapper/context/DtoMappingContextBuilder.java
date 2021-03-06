package io.github.vincemann.springrapid.core.controller.dtoMapper.context;

import io.github.vincemann.springrapid.core.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Slf4j
//todo rename to Rapid...
public class DtoMappingContextBuilder {
    private List<String> currentRoles = new ArrayList<>();
    private RapidDtoMappingContext mc;
    private DtoMappingInfo.Principal currPrincipal = DtoMappingInfo.Principal.ALL;


    public DtoMappingContextBuilder() {
        this.mc = new RapidDtoMappingContext();
    }

    public DtoMappingContextBuilder(RapidDtoMappingContext mc) {
        this.mc = mc;
    }

    public static DtoMappingContextBuilder builder(){
        return new DtoMappingContextBuilder();
    }

    public static DtoMappingContextBuilder builder(RapidDtoMappingContext mc){
        return new DtoMappingContextBuilder(mc);
    }


    public DtoMappingContextBuilder withRoles(String... roles){
        this.currentRoles =Lists.newArrayList(roles);
        return this;
    }

    public DtoMappingContextBuilder withAllRoles(){
        this.currentRoles = new ArrayList<>();
        return this;
    }

    public DtoMappingContextBuilder withPrincipal(DtoMappingInfo.Principal principal){
        Assert.notNull(principal);
        this.currPrincipal=principal;
        return this;
    }

    public DtoMappingContextBuilder withAllPrincipals(){
        this.currPrincipal= DtoMappingInfo.Principal.ALL;
        return this;
    }


    protected List<String> getAllEndpoints(){
        ArrayList<String> all = Lists.newArrayList(RapidDtoEndpoint.CREATE);
        all.addAll(getWriteEndpoints());
        all.addAll(getFindEndpoints());
        return all;
    }

    protected List<String> getFindEndpoints(){
        return Lists.newArrayList(RapidDtoEndpoint.FIND, RapidDtoEndpoint.FIND_ALL);
    }

    protected List<String> getWriteEndpoints(){
        ArrayList<String> writeEndpoints = Lists.newArrayList(RapidDtoEndpoint.CREATE);
        writeEndpoints.addAll(getUpdateEndpoints());
        return writeEndpoints;
    }

    protected List<String> getUpdateEndpoints(){
        return Lists.newArrayList(RapidDtoEndpoint.UPDATE);
    }

    /**
     * Uses one dto class for all crud operations
     * @param defaultDtoClass
     * @return
     */
    public DtoMappingContextBuilder forAll(Class<?> defaultDtoClass){
        Assert.notNull(defaultDtoClass);
        List<DtoMappingInfo> infoList = createInfos(getAllEndpoints());
        for (DtoMappingInfo info : infoList) {
            addEntry(info,defaultDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forResponse(Class<?> responseDtoClass){
        Assert.notNull(responseDtoClass);
        return forDirection(Direction.RESPONSE,responseDtoClass);
    }

    public DtoMappingContextBuilder forRequest(Class<?> responseDtoClass){
        Assert.notNull(responseDtoClass);
        return forDirection(Direction.REQUEST,responseDtoClass);
    }

    private DtoMappingContextBuilder forDirection(Direction direction,Class<?> responseDtoClass){
        Assert.notNull(direction);
        Assert.notNull(responseDtoClass);
        List<String> allEndpoints = getAllEndpoints();
        List<DtoMappingInfo> infoList = new ArrayList<>();
        for (String endpoint : allEndpoints) {
            infoList.add(createInfo(endpoint,direction));
        }
        for (DtoMappingInfo info : infoList) {
            addEntry(info,responseDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forFind(Class<?> readDtoClass){
        Assert.notNull(readDtoClass);
        List<DtoMappingInfo> infoList = createInfos(getFindEndpoints());
        for (DtoMappingInfo info : infoList) {
            addEntry(info,readDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forWrite(Class<?> writeDtoClass){
        Assert.notNull(writeDtoClass);
        List<DtoMappingInfo> infoList = createInfos(getWriteEndpoints());
        for (DtoMappingInfo info : infoList) {
            addEntry(info,writeDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forUpdate(Direction direction,Class<?> updateDtoClass){
        Assert.notNull(updateDtoClass);
        Assert.notNull(direction);
        List<String> updateEndpoints = getUpdateEndpoints();
        for (String updateEndpoint : updateEndpoints) {
            addEntry(createInfo(updateEndpoint,direction),updateDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forUpdate(Class<?> updateDtoClass){
        Assert.notNull(updateDtoClass);
        List<String> updateEndpoints = getUpdateEndpoints();
        for (String updateEndpoint : updateEndpoints) {
            addEntry(createInfo(updateEndpoint,Direction.REQUEST),updateDtoClass);
            addEntry(createInfo(updateEndpoint,Direction.RESPONSE),updateDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forEndpoint(String endpoint, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        addEntry(createInfo(endpoint,Direction.REQUEST),dtoClass);
        addEntry(createInfo(endpoint,Direction.RESPONSE),dtoClass);
        return this;
    }


    public DtoMappingContextBuilder forEndpoint(String endpoint, Direction direction, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        Assert.notNull(direction);
        addEntry(createInfo(endpoint,direction),dtoClass);
        return this;
    }

    public DtoMappingContextBuilder forEndpointAndRoles(String endpoint, Direction direction, List<String> authorities, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        Assert.notNull(direction);
        Assert.notNull(authorities);
        Assert.notNull(dtoClass);
        DtoMappingInfo info = createInfo(endpoint, direction);
        info.setAuthorities(authorities);
        addEntry(info,dtoClass);
        return this;
    }

    public DtoMappingContextBuilder forInfo(DtoMappingInfo info, Class<?> dtoClass){
        Assert.notNull(dtoClass);
        Assert.notNull(info);
        addEntry(info,dtoClass);
        return this;
    }

    protected void addEntry(DtoMappingInfo info, Class<?> dtoClass){
        Class<?> old = mc.getMappingEntries().get(info);
        mc.getMappingEntries().put(info, dtoClass);
        if (old!=null) {
            if (!old.equals(dtoClass)) {
                log.warn("Overriding dto mapping info: " + info + ", old DtoClass: " + old + ", new DtoClass: " + dtoClass);
            }
        }
    }

    public RapidDtoMappingContext build(){
        //create ignore Role Map
//        mc.getMappingEntries().entrySet().forEach(e ->{
//            DtoMappingInfo ignoreRoleInfo = new DtoMappingInfo(e.getKey());
//            ignoreRoleInfo.getAuthorities().clear();
//            mc.getMappingEntriesIgnoreRole()
//                    .computeIfAbsent(ignoreRoleInfo, k -> e.getValue());
//        });
        return mc;
    }


    /**
     * Create info for all given endpoints and for all directions
     * @param endpoints
     * @return
     */
    private List<DtoMappingInfo> createInfos(List<String> endpoints) {
        List<DtoMappingInfo> infoList = new ArrayList<>();
        for (String endpoint : endpoints) {
            infoList.add(createInfo(endpoint,Direction.REQUEST));
            infoList.add(createInfo(endpoint,Direction.RESPONSE));
        }
        return infoList;
    }

    private DtoMappingInfo createInfo(String endpoint, Direction direction){
        return DtoMappingInfo.builder()
                .authorities(currentRoles)
                .endpoint(endpoint)
                .principal(currPrincipal)
                .direction(direction)
                .build();
    }
}
