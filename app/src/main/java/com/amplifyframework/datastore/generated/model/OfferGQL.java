package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the OfferGQL type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "OfferGQLS")
public final class OfferGQL implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField DESCRIPTION = field("description");
  public static final QueryField ADDRESS = field("address");
  public static final QueryField PRICE = field("price");
  public static final QueryField IMAGE_URL = field("imageUrl");
  public static final QueryField OWNER_USERNAME = field("ownerUsername");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String description;
  private final @ModelField(targetType="String", isRequired = true) String address;
  private final @ModelField(targetType="Float", isRequired = true) Float price;
  private final @ModelField(targetType="String", isRequired = true) String imageUrl;
  private final @ModelField(targetType="String", isRequired = true) String ownerUsername;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getDescription() {
      return description;
  }
  
  public String getAddress() {
      return address;
  }
  
  public Float getPrice() {
      return price;
  }
  
  public String getImageUrl() {
      return imageUrl;
  }
  
  public String getOwnerUsername() {
      return ownerUsername;
  }
  
  private OfferGQL(String id, String name, String description, String address, Float price, String imageUrl, String ownerUsername) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.address = address;
    this.price = price;
    this.imageUrl = imageUrl;
    this.ownerUsername = ownerUsername;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      OfferGQL offerGql = (OfferGQL) obj;
      return ObjectsCompat.equals(getId(), offerGql.getId()) &&
              ObjectsCompat.equals(getName(), offerGql.getName()) &&
              ObjectsCompat.equals(getDescription(), offerGql.getDescription()) &&
              ObjectsCompat.equals(getAddress(), offerGql.getAddress()) &&
              ObjectsCompat.equals(getPrice(), offerGql.getPrice()) &&
              ObjectsCompat.equals(getImageUrl(), offerGql.getImageUrl()) &&
              ObjectsCompat.equals(getOwnerUsername(), offerGql.getOwnerUsername());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getDescription())
      .append(getAddress())
      .append(getPrice())
      .append(getImageUrl())
      .append(getOwnerUsername())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("OfferGQL {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("address=" + String.valueOf(getAddress()) + ", ")
      .append("price=" + String.valueOf(getPrice()) + ", ")
      .append("imageUrl=" + String.valueOf(getImageUrl()) + ", ")
      .append("ownerUsername=" + String.valueOf(getOwnerUsername()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static OfferGQL justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new OfferGQL(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      description,
      address,
      price,
      imageUrl,
      ownerUsername);
  }
  public interface NameStep {
    DescriptionStep name(String name);
  }
  

  public interface DescriptionStep {
    AddressStep description(String description);
  }
  

  public interface AddressStep {
    PriceStep address(String address);
  }
  

  public interface PriceStep {
    ImageUrlStep price(Float price);
  }
  

  public interface ImageUrlStep {
    OwnerUsernameStep imageUrl(String imageUrl);
  }
  

  public interface OwnerUsernameStep {
    BuildStep ownerUsername(String ownerUsername);
  }
  

  public interface BuildStep {
    OfferGQL build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements NameStep, DescriptionStep, AddressStep, PriceStep, ImageUrlStep, OwnerUsernameStep, BuildStep {
    private String id;
    private String name;
    private String description;
    private String address;
    private Float price;
    private String imageUrl;
    private String ownerUsername;
    @Override
     public OfferGQL build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new OfferGQL(
          id,
          name,
          description,
          address,
          price,
          imageUrl,
          ownerUsername);
    }
    
    @Override
     public DescriptionStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public AddressStep description(String description) {
        Objects.requireNonNull(description);
        this.description = description;
        return this;
    }
    
    @Override
     public PriceStep address(String address) {
        Objects.requireNonNull(address);
        this.address = address;
        return this;
    }
    
    @Override
     public ImageUrlStep price(Float price) {
        Objects.requireNonNull(price);
        this.price = price;
        return this;
    }
    
    @Override
     public OwnerUsernameStep imageUrl(String imageUrl) {
        Objects.requireNonNull(imageUrl);
        this.imageUrl = imageUrl;
        return this;
    }
    
    @Override
     public BuildStep ownerUsername(String ownerUsername) {
        Objects.requireNonNull(ownerUsername);
        this.ownerUsername = ownerUsername;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String description, String address, Float price, String imageUrl, String ownerUsername) {
      super.id(id);
      super.name(name)
        .description(description)
        .address(address)
        .price(price)
        .imageUrl(imageUrl)
        .ownerUsername(ownerUsername);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder address(String address) {
      return (CopyOfBuilder) super.address(address);
    }
    
    @Override
     public CopyOfBuilder price(Float price) {
      return (CopyOfBuilder) super.price(price);
    }
    
    @Override
     public CopyOfBuilder imageUrl(String imageUrl) {
      return (CopyOfBuilder) super.imageUrl(imageUrl);
    }
    
    @Override
     public CopyOfBuilder ownerUsername(String ownerUsername) {
      return (CopyOfBuilder) super.ownerUsername(ownerUsername);
    }
  }
  
}
