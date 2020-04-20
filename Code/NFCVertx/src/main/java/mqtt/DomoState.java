package mqtt;

import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DomoState {

	private static final AtomicInteger COUNTER = new AtomicInteger();
	private final int id;
	private String name;
	private String type;
	private String value;

	@JsonCreator
	public DomoState(@JsonProperty("name") String name, @JsonProperty("type") String type,
			@JsonProperty("value") String value) {
		this.id = COUNTER.getAndIncrement();
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public DomoState() {
		this.id = COUNTER.getAndIncrement();
		this.name = "";
		this.type = "";
		this.value = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomoState other = (DomoState) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
