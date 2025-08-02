package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionRepository extends JpaRepository<Conversion, Integer> {
    // This method utilizes Automatic Custom Queries, so it should fetch from the Conversion table 
    // without the need to write any code
    // See https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa#1-automatic-custom-queries 
    // for more details
    public Conversion findByFromAndTo(String from, String to);
}
