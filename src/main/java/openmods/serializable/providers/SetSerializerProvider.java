package openmods.serializable.providers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import openmods.reflection.TypeUtils;
import openmods.serializable.IGenericSerializerProvider;
import openmods.utils.io.IStreamSerializer;

public class SetSerializerProvider implements IGenericSerializerProvider {

    @Override
    public IStreamSerializer<?> getSerializer(Type type) {
        TypeToken<?> typeToken = TypeToken.of(type);

        if (TypeUtils.SET_TOKEN.isAssignableFrom(typeToken)) {
            final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.SET_VALUE_PARAM);
            return createSetSerializer(componentType);
        }

        return null;
    }

    protected IStreamSerializer<?> createSetSerializer(final TypeToken<?> componentType) {
        final IStreamSerializer<Object[]> arraySerializer = NullableCollectionSerializer
                .createObjectArraySerializer(componentType);
        return new IStreamSerializer<Set<?>>() {

            @Override
            public Set<?> readFromStream(DataInput input) throws IOException {
                Object[] values = arraySerializer.readFromStream(input);
                return Sets.newHashSet(values);
            }

            @Override
            public void writeToStream(Set<?> o, DataOutput output) throws IOException {
                final Object[] tmp = new Object[o.size()];
                arraySerializer.writeToStream(o.toArray(tmp), output);
            }
        };
    }

}
