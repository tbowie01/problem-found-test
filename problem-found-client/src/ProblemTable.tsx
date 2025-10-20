import {
  createColumnHelper,
  getCoreRowModel,
  useReactTable,
  flexRender,
} from "@tanstack/react-table";
import { useEffect, useMemo, useState } from "react";
import "./ProblemTable.css";

type Problem = {
  source: string;
  text: string;
  url: string;
  date: Date | null;
};

type ProblemDTO = {
  externalId: string;
  source: string;
  text: string;
  url: string;
  sourceCreated: string | null;
  keywords: string[];
};

type PaginationState = {
  pageIndex: number;
  pageSize: number;
};

type ResultsInfo = {
  pageCount: number;
  rowCount: number;
};

const columnHelper = createColumnHelper<Problem>();

const columns = [
  columnHelper.accessor((row) => row.text, {
    id: "text",
    header: () => "Problem",
    cell: (info) => info.getValue(),
    meta: {
      headerClass: "pf-table__head--problem",
      cellClass: "pf-table__cell--problem",
    },
  }),
  columnHelper.accessor((row) => row.source, {
    id: "source",
    header: () => "Source",
    cell: (info) => info.getValue(),
    meta: {
      headerClass: "pf-table__head--source",
      cellClass: "pf-table__cell--source",
    },
  }),
  columnHelper.accessor((row) => row.url, {
    id: "url",
    header: () => "Original URL",
    cell: (info) => (
      <a
        href={info.getValue()}
        target="_blank"
        rel="noreferrer"
        className="pf-table__link"
      >
        View
      </a>
    ),
    meta: {
      headerClass: "pf-table__head--url",
      cellClass: "pf-table__cell--url",
    },
  }),
  columnHelper.accessor((row) => row.date, {
    id: "date",
    header: () => "Posted Date",
    cell: (info) => {
      const date = info.getValue();
      if (!date) {
        return "—";
      }

      return date.toLocaleDateString(undefined, {
        month: "short",
        day: "numeric",
        year: "numeric",
      });
    },
    meta: {
      headerClass: "pf-table__head--date",
      cellClass: "pf-table__cell--date",
    },
  }),
];

export default function ProblemTable() {
  const [data, setData] = useState<Problem[]>([]);
  const [resultsInfo, setResultsInfo] = useState<ResultsInfo>({
    pageCount: 0,
    rowCount: 0,
  });

  const [pagination, setPagination] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });

  useEffect(() => {
    const params = new URLSearchParams({
      pageIndex: pagination.pageIndex.toString(),
      size: pagination.pageSize.toString(),
    });

    fetch(`http://localhost:8080/api/problems?${params}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP Error Status: ${response.status}`);
        }
        return response.json();
      })
      .then((payload) => {
        const content = payload.content as ProblemDTO[];
        const problems = content.map((contentObj) => {
          const parsedDate = contentObj.sourceCreated
            ? new Date(contentObj.sourceCreated)
            : null;
          const isValidDate =
            parsedDate !== null && !Number.isNaN(parsedDate.getTime());

          const problem: Problem = {
            source: contentObj.source,
            text: contentObj.text,
            url: contentObj.url,
            date: isValidDate ? parsedDate : null,
          };

          return problem;
        });

        setResultsInfo({
          pageCount: payload.totalPages,
          rowCount: payload.totalElements,
        });
        setData(problems);
      })
      .catch((error) => {
        console.error(error);
      });
  }, [pagination.pageIndex, pagination.pageSize]);

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    onPaginationChange: setPagination,
    manualPagination: true,
    pageCount: resultsInfo.pageCount,
    state: {
      pagination,
    },
  });

  const isEmpty = data.length === 0;

  const pageWindow = useMemo(() => {
    if (!resultsInfo.rowCount || isEmpty) {
      return { start: 0, end: 0 };
    }

    const start = pagination.pageIndex * pagination.pageSize + 1;
    const end = start + data.length - 1;

    return { start, end };
  }, [data.length, isEmpty, pagination.pageIndex, pagination.pageSize, resultsInfo.rowCount]);

  return (
    <div className="pf-table">
      <div className="pf-table__summary">
        <div className="pf-table__summary-count">
          {isEmpty
            ? "No problems captured yet."
            : `Showing ${pageWindow.start.toLocaleString()} – ${pageWindow.end.toLocaleString()} of ${resultsInfo.rowCount.toLocaleString()} problems`}
        </div>
        <div className="pf-table__summary-page">
          Page {table.getState().pagination.pageIndex + 1} of{" "}
          {Math.max(resultsInfo.pageCount, 1).toLocaleString()}
        </div>
      </div>

      <div className="pf-table__wrapper">
        <table className="pf-table__grid">
          <thead>
            {table.getHeaderGroups().map((headerGroup) => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <th
                    key={header.id}
                    className={[
                      "pf-table__head",
                      (
                        header.column.columnDef
                          .meta as { headerClass?: string } | undefined
                      )?.headerClass,
                    ].filter(Boolean).join(" ")}
                  >
                    {header.isPlaceholder
                      ? null
                      : flexRender(
                          header.column.columnDef.header,
                          header.getContext()
                        )}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody>
            {table.getRowModel().rows.map((row) => (
              <tr key={row.id} className="pf-table__row">
                {row.getVisibleCells().map((cell) => (
                  <td
                    key={cell.id}
                    className={[
                      "pf-table__cell",
                      (
                        cell.column.columnDef.meta as {
                          cellClass?: string;
                        } | undefined
                      )?.cellClass,
                    ].filter(Boolean).join(" ")}
                  >
                    {flexRender(
                      cell.column.columnDef.cell,
                      cell.getContext()
                    )}
                  </td>
                ))}
              </tr>
            ))}
            {isEmpty ? (
              <tr>
                <td colSpan={columns.length} className="pf-table__empty">
                  Start the Reddit collector to populate live problems.
                </td>
              </tr>
            ) : null}
          </tbody>
        </table>
      </div>

      <div className="pf-table__pagination">
        <div className="pf-table__pagination-controls">
          <button
            type="button"
            className="pf-table__button"
            onClick={() => table.firstPage()}
            disabled={!table.getCanPreviousPage()}
            aria-label="Go to first page"
          >
            {"<<"}
          </button>
          <button
            type="button"
            className="pf-table__button"
            onClick={() => table.previousPage()}
            disabled={!table.getCanPreviousPage()}
            aria-label="Go to previous page"
          >
            {"<"}
          </button>
          <button
            type="button"
            className="pf-table__button"
            onClick={() => table.nextPage()}
            disabled={!table.getCanNextPage()}
            aria-label="Go to next page"
          >
            {">"}
          </button>
          <button
            type="button"
            className="pf-table__button"
            onClick={() => table.lastPage()}
            disabled={!table.getCanNextPage()}
            aria-label="Go to last page"
          >
            {">>"}
          </button>
        </div>

        <div className="pf-table__pagination-cta">
          <label className="pf-table__label">
            Go to page
            <input
              type="number"
              min="1"
              max={Math.max(resultsInfo.pageCount, 1)}
              defaultValue={table.getState().pagination.pageIndex + 1}
              onChange={(e) => {
                const page = e.target.value ? Number(e.target.value) - 1 : 0;
                table.setPageIndex(page);
              }}
              className="pf-table__input"
            />
          </label>

          <label className="pf-table__label">
            Show
            <select
              value={table.getState().pagination.pageSize}
              onChange={(event) => {
                const value = Number(
                  (event.target as HTMLSelectElement).value
                );
                table.setPageSize(value);
              }}
              className="pf-table__select"
            >
              {[10, 20, 30, 40, 50].map((pageSize) => (
                <option key={pageSize} value={pageSize}>
                  {pageSize} rows
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>
    </div>
  );
}
