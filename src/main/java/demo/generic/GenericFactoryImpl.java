package demo.generic;
                            //N,T或者是T,N都行
public class GenericFactoryImpl<N,T> implements GenericIFactory<T,N> {
    @Override
    public T nextObject() {
        return null;
    }

    @Override
    public N nextNumber() {
        return null;
    }

}
