package team9.demo.util;

import org.springframework.data.domain.Sort;

public enum SortType {
    LATEST {
        @Override
        public Sort toSort() {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

    },

    OLDEST {
        @Override
        public Sort toSort() {
            return Sort.by(Sort.Direction.ASC, "createdAt");
        }
    },

    SMALLEST {
        @Override
        public Sort toSort() {
            return Sort.by(Sort.Direction.ASC, "sequence");
        }
    },

    LARGEST {
        @Override
        public Sort toSort() {
            return Sort.by(Sort.Direction.DESC, "sequence");
        }
    },

    SEQUENCE_ASC {
        @Override
        public Sort toSort() {
            return Sort.by(Sort.Direction.ASC, "sequence");
        }
    },

    SEQUENCE_DESC {
        @Override
        public Sort toSort() {
            return Sort.by(Sort.Direction.DESC, "sequence");
        }
    };

    public abstract Sort toSort();

}
