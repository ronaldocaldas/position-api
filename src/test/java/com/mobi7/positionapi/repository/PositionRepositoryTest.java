package com.mobi7.positionapi.repository;

import com.mobi7.positionapi.model.Position;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PositionRepositoryTest {

    @Autowired
    private PositionRepository positionRepository;

    private static EasyRandom generator;

    @BeforeAll
    static void beforeAll() {
        setUpRandomGenerator();
    }

    private static void setUpRandomGenerator() {
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(2, 6);
        generator = new EasyRandom(parameters);
    }

    private Position createRandomPosition() {
        return generator.nextObject(Position.class);
    }

    @Test
    @DisplayName("Should create position on H2 database with success")
    void createPositionTest() throws Exception {
        // Given
        Position position = createRandomPosition();

        // When
        positionRepository.save(position);

        // Then
        Position retrievedPosition = positionRepository.findById(position.getPositionId()).orElse(null);
        assertThat(retrievedPosition).isNotNull();
        assertThat(retrievedPosition).isEqualTo(position);
    }
}
