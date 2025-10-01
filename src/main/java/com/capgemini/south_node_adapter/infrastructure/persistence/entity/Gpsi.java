package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("gpsi")
@Getter
@Setter
public class Gpsi {
  String ue;
}
